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

import com.fasterxml.jackson.databind.ObjectMapper;

import gomobi.io.forex.service.NseIndiaService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/nse")
@CrossOrigin // Enable CORS for Angular
public class NseIndiaController {
	
	 @Autowired
	 private NseIndiaService nseIndiaService;
	 	
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
	     return nseIndiaService.getTopGainersAndLosers(indexSymbol);
	 }
	 
	// Modified 6. get most active 
	@GetMapping("/mostActive/{indexSymbol}")
	public Mono<String> getMostActive(@PathVariable String indexSymbol){
		return nseIndiaService.getMostActiveEquities(indexSymbol);
	}

	//7. get equity chart details( for candle )
	@GetMapping("/getChart/{symbol}")
	public Mono<String> getChartPoints(@PathVariable String symbol){
		return nseIndiaService.getChartPoints(symbol,false);
	}
	
	//7. get equity chart details( for candle ) with pre-open data
	@GetMapping("/getChartWithPO/{symbol}")
	public Mono<String> getChartPointsWithPreopen(@PathVariable String symbol){
		return nseIndiaService.getChartPoints(symbol,true);
	}
}
