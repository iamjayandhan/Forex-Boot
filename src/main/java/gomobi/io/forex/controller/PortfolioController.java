package gomobi.io.forex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gomobi.io.forex.dto.BuyRequestDTO;
import gomobi.io.forex.dto.SellRequestDTO;
import gomobi.io.forex.service.PortfolioService;



@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;
    
    //portfolio endpoint! heart of the application!
    @GetMapping("/{userId}")
    public ResponseEntity<?> getPortfolio(@PathVariable Long userId){
    	return portfolioService.getUserPortfolio(userId);
    }

    //Buy Stock
    @PostMapping("/buy")
    public ResponseEntity<?> buyStock(@RequestBody BuyRequestDTO buyRequest) {
    	return portfolioService.buyStock(buyRequest);
    }
    
    //Sell Stock
    @PostMapping("/sell")
    public ResponseEntity<?> sellStock(@RequestBody SellRequestDTO sellRequest){
    	return portfolioService.sellStock(sellRequest);
    }

    //Get Holdings
    @GetMapping("/holdings/{userId}")
    public ResponseEntity<?> getUserHoldings(@PathVariable Long userId) {
        return portfolioService.getUserHoldings(userId);
    }

    //Get Transactions
    @GetMapping("/transactions/{userId}")
    public ResponseEntity<?> getUserTransactions(@PathVariable Long userId) {
        return portfolioService.getUserTransactions(userId);
    }

    //portfolio endpoint!
}
