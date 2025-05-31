package gomobi.io.forex.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gomobi.io.forex.model.IndexEquityInfo;
import gomobi.io.forex.service.NseIndiaService;
import gomobi.io.forex.util.IndexEquityInfoMapper;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/nse")
@CrossOrigin // Enable CORS for Angular
public class NseIndiaController {
	
	 @Autowired
	 private NseIndiaService nseIndiaService;
	 
	 private final ObjectMapper objectMapper = new ObjectMapper();
	
	 //1. get stock info
	 @GetMapping("/equity-details/{symbol}")
	 public Mono<String> getEquityDetails(@PathVariable String symbol) {
	     return nseIndiaService.getEquityDetails(symbol);
	 }
	 
	 //2. get stock intraday info
	 @GetMapping("/equity-details/intraday/{symbol}")
	 public Mono<String> getEquityIntradayDetails(@PathVariable String symbol) {
	     return nseIndiaService.getEquityIntradayDetails(symbol);
	 }
	 
	 //3. get stock announcements
	 @GetMapping("/equity-details/announcements/{symbol}")
	 public Mono<String> getEquityAnnouncements(@PathVariable String symbol) {
	     return nseIndiaService.getEquityAnnouncements(symbol);
	 }
	 
	 //4. get market status
	 @GetMapping("/getMarketStatus")
	 public Mono<String> getMarketStatus(){
		 return nseIndiaService.getMarketStatus();
	 }
	 
	// Modified 5. get top gainers and losers by index symbol
	@GetMapping("/gainersAndLosers/{indexSymbol}")
	public Mono<String> getTopGainersAndLosers(@PathVariable String indexSymbol) {
	    return nseIndiaService.getTGL(indexSymbol)
	        .flatMap(responseBody -> {
	            try {
	                JsonNode rootNode = objectMapper.readTree(responseBody);
	                JsonNode dataNode = rootNode.path("data");
	
	                if (!dataNode.isArray()) {
	                    return Mono.just("{\"error\":\"Invalid data format\"}");
	                }
	
	                List<IndexEquityInfo> equities = Arrays.asList(
	                    objectMapper.treeToValue(dataNode, IndexEquityInfo[].class)
	                );
	
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
	
	                Map<String, Object> response = new HashMap<>();
	                response.put("gainers", gainers);
	                response.put("losers", losers);
	
	                return Mono.just(objectMapper.writeValueAsString(response));
	            } catch (Exception e) {
	                e.printStackTrace();
	                return Mono.just("{\"error\":\"" + e.getMessage() + "\"}");
	            }
	        });
	}
	
	//6. Most active stocks
	@GetMapping("/mostActive/{indexSymbol}")
	public Mono<String> getMostActiveEquities(@PathVariable String indexSymbol) {
	    return nseIndiaService.getTGL(indexSymbol)
	        .flatMap(responseBody -> {
	            try {
	                JsonNode rootNode = objectMapper.readTree(responseBody);
	                JsonNode dataNode = rootNode.path("data");

	                if (!dataNode.isArray()) {
	                    return Mono.just("{\"error\":\"Invalid data format\"}");
	                }

	                List<IndexEquityInfo> equities = Arrays.asList(
	                    objectMapper.treeToValue(dataNode, IndexEquityInfo[].class)
	                );

	                // Sort by volume descending
	                List<Map<String, Object>> byVolume = equities.stream()
	                    .sorted((a, b) -> Long.compare(b.getTotalTradedVolume(), a.getTotalTradedVolume()))
	                    .map(IndexEquityInfoMapper::enrich)
	                    .toList();

	                // Sort by value descending
	                List<Map<String, Object>> byValue = equities.stream()
	                    .sorted((a, b) -> Double.compare(b.getTotalTradedValue(), a.getTotalTradedValue()))
	                    .map(IndexEquityInfoMapper::enrich)
	                    .toList();

	                Map<String, Object> response = new HashMap<>();
	                response.put("byVolume", byVolume);
	                response.put("byValue", byValue);

	                return Mono.just(objectMapper.writeValueAsString(response));

	            } catch (Exception e) {
	                e.printStackTrace();
	                return Mono.just("{\"error\":\"" + e.getMessage() + "\"}");
	            }
	        });
	}


	 
}
