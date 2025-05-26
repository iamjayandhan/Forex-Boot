package gomobi.io.forex.service;

import org.springframework.http.ResponseEntity;

import gomobi.io.forex.dto.BuyRequestDTO;
import gomobi.io.forex.dto.SellRequestDTO;


public interface PortfolioService {
    ResponseEntity<?> buyStock(BuyRequestDTO buyRequest);
    ResponseEntity<?> sellStock(SellRequestDTO sellRequest);
    
    ResponseEntity<?> getUserHoldings(Long userId);
    ResponseEntity<?> getUserTransactions(Long userId);
    ResponseEntity<?> getUserTransactionsPaginated(Long userId, int page, int size);
}
