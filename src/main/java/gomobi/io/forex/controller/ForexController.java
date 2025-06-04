package gomobi.io.forex.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/forex")
public class ForexController {
	
	@Value("${twelvedata.api.key}")
	String apiKey;
	
    @GetMapping("/history")
    public ResponseEntity<String> getHistoricalData(
        @RequestParam String symbol,
        @RequestParam(defaultValue = "1min") String interval
    ) {
        String url = String.format("https://api.twelvedata.com/time_series?symbol=%s&interval=%s&apikey=%s", 
                                   symbol, interval, apiKey);
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        return ResponseEntity.ok(response);
    }
}
