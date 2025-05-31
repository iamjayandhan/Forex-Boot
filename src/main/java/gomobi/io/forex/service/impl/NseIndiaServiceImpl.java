//package gomobi.io.forex.service.impl;
//
//import java.time.Duration;
//import java.util.List;
//import java.util.concurrent.TimeoutException;
//import java.util.concurrent.atomic.AtomicReference;
//import java.util.stream.Collectors;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//
//import gomobi.io.forex.service.NseIndiaService;
//import reactor.core.publisher.Mono;
//
//@Service
//public class NseIndiaServiceImpl implements NseIndiaService {
//
//    private static final Logger logger = LoggerFactory.getLogger(NseIndiaServiceImpl.class);
//
//    private final WebClient webClient;
//    private final AtomicReference<String> cookies = new AtomicReference<>("");
//    private final AtomicReference<Long> lastRefreshTime = new AtomicReference<>(0L);
//    private final AtomicReference<String> userAgent = new AtomicReference<>(
//        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
//        "(KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");
//
//    private static final long COOKIE_EXPIRY_MS = Duration.ofMinutes(5).toMillis();
//
//    public NseIndiaServiceImpl() {
//        this.webClient = WebClient.builder()
//                .baseUrl("https://www.nseindia.com")
//                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
//                .defaultHeader(HttpHeaders.REFERER, "https://www.nseindia.com/")
//                .defaultHeader(HttpHeaders.USER_AGENT, userAgent.get())
//                .build();
//    }
//
//    private boolean isCookieStale() {
//        return cookies.get().isEmpty() ||
//               (System.currentTimeMillis() - lastRefreshTime.get()) > COOKIE_EXPIRY_MS;
//    }
//
//    private Mono<Object> refreshCookies() {
//        logger.info("Refreshing NSE cookies...");
//
//        return webClient.get()
////                .uri("/get-quotes/equity?symbol=TCS")
//        		.uri("/api/option-chain-indices?symbol=NIFTY")
//                .exchangeToMono(response -> {
//                    List<String> setCookies = response.headers().header(HttpHeaders.SET_COOKIE);
//                    if (!setCookies.isEmpty()) {
//                    	String joinedCookies = setCookies.stream()
//                    		    .map(c -> c.split(";", 2)[0])
//                    		    .collect(Collectors.joining("; "));
//
//                        cookies.set(joinedCookies);
//                        lastRefreshTime.set(System.currentTimeMillis());
//                        logger.info("Cookies refreshed successfully.");
//                    } else {
//                        logger.warn("No cookies received during refresh.");
//                    }
//                    return Mono.empty();
//                })
//                .timeout(Duration.ofSeconds(10))
//                .onErrorResume(error -> {
//                    logger.error("Failed to refresh cookies: {}", error.getMessage());
//                    return Mono.empty();
//                });
//    }
//
//    private Mono<String> fetchFromNseApi(String apiUrl) {
//        if (isCookieStale()) {
//            return refreshCookies()
//                    .then(fetchFromNseApiWithCookies(apiUrl));
//        } else {
//            return fetchFromNseApiWithCookies(apiUrl)
//                    .onErrorResume(error -> {
//                        logger.warn("Error during fetch: {}, retrying after refreshing cookies.", error.getMessage());
//                        return refreshCookies()
//                                .then(fetchFromNseApiWithCookies(apiUrl));
//                    });
//        }
//    }
//
//    private Mono<String> fetchFromNseApiWithCookies(String apiUrl) {
//        logger.info("Fetching NSE API: {}", apiUrl);
//
//        return webClient.get()
//                .uri(apiUrl)
//                .header(HttpHeaders.COOKIE, cookies.get())
//                .header(HttpHeaders.USER_AGENT, userAgent.get())
//                .retrieve()
//                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
//                        response -> response.bodyToMono(String.class).flatMap(body -> {
//                            logger.error("NSE API error ({}): {}", response.statusCode(), body);
//                            return Mono.error(new RuntimeException("NSE API Error: " + body));
//                        }))
//                .bodyToMono(String.class)
//                .timeout(Duration.ofSeconds(10))
//                .onErrorResume(TimeoutException.class, e -> {
//                    logger.error("Timeout fetching NSE API: {}", apiUrl);
//                    return Mono.error(new RuntimeException("Timeout fetching data from NSE"));
//                });
//    }
//    
//    @Override
//    public Mono<String> testEndpoint(String url) {
//        return fetchFromNseApi(url);
//    }
//
//    @Override
//    public Mono<String> getMarketStatus() {
//        return fetchFromNseApi("/api/market-status");
//    }
//
//    @Override
//    public Mono<String> getCirculars() {
//        return fetchFromNseApi("/api/circulars");
//    }
//
//    @Override
//    public Mono<String> getAllIndices() {
//        return fetchFromNseApi("/api/allindices");
//    }
//
//    @Override
//    public Mono<String> getEquityDetails(String symbol) {
//        return fetchFromNseApi("/api/equity/" + symbol.toUpperCase());
//    }
//
//    @Override
//    public Mono<String> getEquityIntraday(String symbol) {
//        return fetchFromNseApi("/api/equity/intraday/" + symbol.toUpperCase());
//    }
//
//    @Override
//    public Mono<String> getGainersAndLosers(String indexSymbol) {
//        return fetchFromNseApi("/api/gainersandlosers/" + indexSymbol.toUpperCase());
//    }
//
//    @Override
//    public Mono<String> getMostActive(String indexSymbol) {
//        return fetchFromNseApi("/api/mostactive/" + indexSymbol.toUpperCase());
//    }
//}
