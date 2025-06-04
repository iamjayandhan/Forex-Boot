package gomobi.io.forex.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gomobi.io.forex.model.IndexEquityInfo;
import gomobi.io.forex.util.IndexEquityInfoMapper;
import reactor.core.publisher.Mono;

@Service
public class NseIndiaService {

    private final WebClient webClient;
    private final AtomicReference<String> cookies = new AtomicReference<>("");
    private final AtomicReference<String> userAgent = new AtomicReference<>("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");

    //for that candle api, we need stock info first. then we extract identifier!
    private static final ObjectMapper objectMapper = new ObjectMapper();

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
    
    //5. get top gainers and losers
    public Mono<String> getTopGainersAndLosers(String indexSymbol) {
        String url = "/api/equity-stockIndices?index=" + indexSymbol;

        return safeRequest(url)
            .flatMap(responseBody -> {
                try {
                    JsonNode rootNode = objectMapper.readTree(responseBody);
                    JsonNode dataNode = rootNode.path("data");

                    if (!dataNode.isArray()) {
                        return Mono.just("{\"error\":\"Invalid data format\"}");
                    }

                    // Convert JSON array to POJOs
                    List<IndexEquityInfo> equities = Arrays.asList(
                        objectMapper.treeToValue(dataNode, IndexEquityInfo[].class)
                    );

                    // Split into gainers and losers
                    List<Map<String, Object>> gainers = equities.stream()
                        .filter(e -> e.getPChange() > 0)
                        .sorted((a, b) -> Double.compare(b.getPChange(), a.getPChange()))
                        .map(IndexEquityInfoMapper::enrich)
                        .toList();

                    List<Map<String, Object>> losers = equities.stream()
                        .filter(e -> e.getPChange() <= 0)
                        .sorted((a, b) -> Double.compare(a.getPChange(), b.getPChange()))
                        .map(IndexEquityInfoMapper::enrich)
                        .toList();

                    // Prepare final response map
                    Map<String, Object> result = new HashMap<>();
                    result.put("gainers", gainers);
                    result.put("losers", losers);

                    // Convert back to JSON
                    String json = objectMapper.writeValueAsString(result);
                    return Mono.just(json);

                } catch (Exception e) {
                    e.printStackTrace();
                    return Mono.just("{\"error\":\"" + e.getMessage() + "\"}");
                }
            });
    }
    
    // 6. Get most active stocks with sorting logic (volume + value)
    public Mono<String> getMostActiveEquities(String indexSymbol) {
        String url = "/api/equity-stockIndices?index=" + URLEncoder.encode(indexSymbol.toUpperCase(), StandardCharsets.UTF_8);

        return safeRequest(url).flatMap(responseBody -> {
            try {
                JsonNode rootNode = objectMapper.readTree(responseBody);
                JsonNode dataNode = rootNode.path("data");

                if (!dataNode.isArray()) {
                    return Mono.just("{\"error\":\"Invalid data format\"}");
                }

                List<IndexEquityInfo> equities = Arrays.asList(
                    objectMapper.treeToValue(dataNode, IndexEquityInfo[].class)
                );

                // Sort and enrich
                List<Map<String, Object>> byVolume = equities.stream()
                    .sorted((a, b) -> Long.compare(b.getTotalTradedVolume(), a.getTotalTradedVolume()))
                    .map(IndexEquityInfoMapper::enrich)
                    .toList();

                List<Map<String, Object>> byValue = equities.stream()
                    .sorted((a, b) -> Double.compare(b.getTotalTradedValue(), a.getTotalTradedValue()))
                    .map(IndexEquityInfoMapper::enrich)
                    .toList();

                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("byVolume", byVolume);
                responseMap.put("byValue", byValue);

                String jsonResponse = objectMapper.writeValueAsString(responseMap);
                return Mono.just(jsonResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return Mono.just("{\"error\":\"" + e.getMessage() + "\"}");
            }
        });
    }

    
    //7. get chart data
    public Mono<String> getChartPoints(String symbol, boolean withPreopen){
    return getEquityDetails(symbol)
        .flatMap(jsonString -> {
            try {
                JsonNode root = objectMapper.readTree(jsonString);
                String identifier = root.path("info").path("identifier").asText();

                String url = "/api/chart-databyindex?index=" + identifier.toUpperCase();
                if (withPreopen) {
                    url += "&preopen=true";
                }

                return safeRequest(url);
            } catch (Exception e) {
                return Mono.error(new RuntimeException("Failed to parse JSON or extract identifier", e));
            }
        });
    }
}
