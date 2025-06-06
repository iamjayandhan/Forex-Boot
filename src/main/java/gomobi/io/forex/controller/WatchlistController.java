package gomobi.io.forex.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gomobi.io.forex.dto.SuccessResponse;
import gomobi.io.forex.entity.StockEntity;
import gomobi.io.forex.entity.Watchlist;
import gomobi.io.forex.repository.WatchlistRepository;
import gomobi.io.forex.service.WatchlistService;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {

    @Autowired
    private WatchlistService watchlistService;

    @Autowired
    private WatchlistRepository watchlistRepository;
    
    @GetMapping("/{userId}")
    public ResponseEntity<?> getWatchlist(@PathVariable Long userId) {
    	if(watchlistRepository.existsByUserId(userId)) {
    		List<StockEntity> items = watchlistService.getUserWatchlist(userId);
    		
    		SuccessResponse<Object> responseBody = new SuccessResponse<Object>(HttpStatus.ACCEPTED.value(),"User wishlist is fetched successfully.",items);
        	return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseBody);
    	}
    	
    	SuccessResponse<Object> responseBody = new SuccessResponse<Object>(HttpStatus.ACCEPTED.value(),"User dont currently hold a watchlist.");
    	return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseBody);      
    }

    @PostMapping
    public ResponseEntity<?> addToWatchlist(@RequestBody List<Map<String, Long>> bodyList) {
    	List<Watchlist> addedItems = new ArrayList<>();
    	
    	for (Map<String, Long> body : bodyList) {
    		Long userId = body.get("userId");
    		Long stockId = body.get("stockId");
    		
    		Watchlist entry = watchlistService.addToWatchlist(userId, stockId);
    		addedItems.add(entry);
    	}
        
        SuccessResponse<Object> responseBody = new SuccessResponse<Object>(HttpStatus.ACCEPTED.value(),"Successfully added to watchlist.",addedItems);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseBody);
    }
    
    @DeleteMapping
    public ResponseEntity<?> removeFromWatchlist(@RequestBody Map<String, Long> body) {
        Long userId = body.get("userId");
        Long stockId = body.get("stockId");
        watchlistService.removeFromWatchlist(userId, stockId);
        return ResponseEntity.noContent().build();
    }
}
