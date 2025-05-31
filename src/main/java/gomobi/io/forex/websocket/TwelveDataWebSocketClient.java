package gomobi.io.forex.websocket;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class TwelveDataWebSocketClient extends WebSocketClient {

    private ScheduledExecutorService heartbeatExecutor;
    private SimpMessagingTemplate  messagingTemplate;

    public TwelveDataWebSocketClient(URI serverUri, SimpMessagingTemplate  messagingTemplate) {
        super(serverUri);
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
//        System.out.println("WebSocket connection opened!");
        // Subscribe message
        String subscribeMessage = """
            {
              "action": "subscribe",
              "params": {
                "symbols": "AAPL,INFY,TRP,QQQ,IXIC,EUR/USD,XAU/USD,USD/JPY,BTC/USD,ETH/BTC"
              }
            }
            """;
        send(subscribeMessage);

        // Schedule heartbeat every ~10 seconds
        heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();
        heartbeatExecutor.scheduleAtFixedRate(() -> {
            send("{\"action\": \"heartbeat\"}");
        }, 10, 10, TimeUnit.SECONDS);
    }

    @Override
    public void onMessage(String message) {
//        System.out.println("Received: " + message);
        // Forward raw or parsed message to angular clients
        messagingTemplate.convertAndSend("/topic/price-updates",message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
//        System.out.println("WebSocket closed: " + reason);
        if (heartbeatExecutor != null) heartbeatExecutor.shutdown();

        // Reconnect after 5 seconds
        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            try {
                this.reconnectBlocking();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 5, TimeUnit.SECONDS);
    }


    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }
}
