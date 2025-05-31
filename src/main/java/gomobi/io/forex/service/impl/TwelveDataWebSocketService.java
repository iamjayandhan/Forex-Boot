package gomobi.io.forex.service.impl;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import gomobi.io.forex.websocket.TwelveDataWebSocketClient;
import jakarta.annotation.PostConstruct;

@Service
public class TwelveDataWebSocketService {

    private TwelveDataWebSocketClient client;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostConstruct
    public void init() {
        try {
            client = new TwelveDataWebSocketClient(new URI("wss://ws.twelvedata.com/v1/quotes/price?apikey=10f2e0080b9640918ba27ee67a428de1"),messagingTemplate);
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
