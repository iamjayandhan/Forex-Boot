package gomobi.io.forex.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
@Service
public class NseIndiaService {

    private final WebClient webClient;
    private final AtomicReference<String> cookies = new AtomicReference<>("");
    private final AtomicReference<String> userAgent = new AtomicReference<>("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");

    public NseIndiaService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://www.nseindia.com")
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.REFERER, "https://www.nseindia.com/")
                .defaultHeader(HttpHeaders.USER_AGENT, userAgent.get())
                .build();
    }

    private Mono<Object> refreshCookies() {
        return webClient.get()
                .uri("/get-quotes/equity?symbol=TCS")
                .exchangeToMono(response -> {
                    List<String> setCookies = response.headers().header(HttpHeaders.SET_COOKIE);
                    if (!setCookies.isEmpty()) {
                        String joinedCookies = String.join("; ", setCookies.stream()
                                .map(cookie -> cookie.split(";")[0])
                                .toList());
                        cookies.set(joinedCookies);
                    }
                    return Mono.empty();
                })
                .timeout(Duration.ofSeconds(10));
    }

    private Mono<String> performNseRequest(String urlPath) {
        return webClient.get()
                .uri(urlPath)
                .header(HttpHeaders.COOKIE, cookies.get())
                .header(HttpHeaders.USER_AGENT, userAgent.get())
                .retrieve()
                .onStatus(status -> status.isError(),
                        response -> response.bodyToMono(String.class).flatMap(body -> {
                            System.err.println("Error from NSE: " + body);
                            return Mono.error(new RuntimeException("NSE API Error: " + body));
                        }))
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(10));
    }

    private Mono<String> safeRequest(String urlPath) {
        if (cookies.get().isEmpty()) {
            return refreshCookies().then(performNseRequest(urlPath));
        } else {
            return performNseRequest(urlPath)
                    .onErrorResume(err -> refreshCookies().then(performNseRequest(urlPath)));
        }
    }

    //Public methods (short, reusable, clean)
    
    //1. get stock info
    public Mono<String> getEquityDetails(String symbol) {
        String url = "/api/quote-equity?symbol=" + symbol.toUpperCase();
        return safeRequest(url);
    }

    //2. get stock intraday info
    public Mono<String> getEquityIntradayDetails(String symbol) {
        String url = "/api/quote-equity?symbol=" + symbol.toUpperCase() + "&section=trade_info";
        return safeRequest(url);
    }
    
    //3. get stock announcements
    public Mono<String> getEquityAnnouncements(String symbol){
    	String url = "api/top-corp-info?symbol="+ symbol.toUpperCase()+"&market=equities";
    	return safeRequest(url);
    }
    
    //4. get market status
    public Mono<String> getMarketStatus(){
    	String url = "api/marketStatus";
    	return safeRequest(url);
    }
    
    //5. get gainers and losers of the specific index
    public Mono<String> getTGL(String indexSymbol){
    	String url = "/api/equity-stockIndices?index="+indexSymbol;
    	System.out.println(url);
    	return safeRequest(url);
    }
    
    //6. get most active stocks
    public Mono<String> getMostActiveEquities(String indexSymbol){
    	String url = "/api/mostActive?index="+indexSymbol;
    	return safeRequest(url);
    }
    

}
