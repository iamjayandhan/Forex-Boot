package gomobi.io.forex.service.impl;

import java.math.BigDecimal;

import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import gomobi.io.forex.dto.BuyRequestDTO;
import gomobi.io.forex.dto.HoldingResponseDTO;
import gomobi.io.forex.dto.SellRequestDTO;
import gomobi.io.forex.dto.StockInfoDTO;
import gomobi.io.forex.dto.SuccessResponse;
import gomobi.io.forex.dto.TransactionResponseDTO;
import gomobi.io.forex.entity.HoldingEntity;
import gomobi.io.forex.entity.StockEntity;
import gomobi.io.forex.entity.TransactionEntity;
import gomobi.io.forex.entity.UserEntity;
import gomobi.io.forex.exception.ErrorResponse;
import gomobi.io.forex.repository.HoldingRepository;
import gomobi.io.forex.repository.StockRepository;
import gomobi.io.forex.repository.TransactionRepository;
import gomobi.io.forex.repository.UserRepository;
import gomobi.io.forex.service.PortfolioService;
import jakarta.transaction.Transactional;

@Service
public class PortfolioServiceImpl implements PortfolioService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private HoldingRepository holdingRepository;

    @Autowired
    private TransactionRepository transactionRepository;
    
    @Override
    @Transactional
    public ResponseEntity<?> buyStock(BuyRequestDTO dto) {
        UserEntity user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        StockEntity stock = stockRepository.findById(dto.getStockId())
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        if (stock.getIpoQty() < dto.getQuantity()) {
            ErrorResponse responseBody = new ErrorResponse(400, "Not enough stock available");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        }

        BigDecimal quantity = BigDecimal.valueOf(dto.getQuantity());
        BigDecimal pricePerUnit = stock.getCurrentPrice();
        BigDecimal backendSubtotal = pricePerUnit.multiply(quantity).setScale(2, RoundingMode.HALF_EVEN);

        // === Charges Calculation ===

        // Brokerage: 0.25% of subtotal or max 20
        BigDecimal brokerage = backendSubtotal.multiply(BigDecimal.valueOf(0.0025));
        if (brokerage.compareTo(BigDecimal.valueOf(20)) > 0) {
            brokerage = BigDecimal.valueOf(20);
        }

        // ETC: Based on exchange
        //NSE - 0.00325% , BSE - 0.0030%
        BigDecimal etc = stock.getExchange().equalsIgnoreCase("NSE")
                ? backendSubtotal.multiply(BigDecimal.valueOf(0.0000325))
                : backendSubtotal.multiply(BigDecimal.valueOf(0.00003));

        // Stamp Duty: 0.015%
        BigDecimal stampDuty = backendSubtotal.multiply(BigDecimal.valueOf(0.00015));

        // IPF: ₹0.0002 per share
        BigDecimal ipf = BigDecimal.valueOf(dto.getQuantity()).multiply(BigDecimal.valueOf(0.0002));

        // SEBI Charges: 0.001%
        BigDecimal sebiCharges = backendSubtotal.multiply(BigDecimal.valueOf(0.00001));

        // STT: 0.025%
        BigDecimal stt = backendSubtotal.multiply(BigDecimal.valueOf(0.00025));

        // GST: 18% on (brokerage + etc + sebi)
        BigDecimal gstBase = brokerage.add(etc).add(sebiCharges);
        BigDecimal gst = gstBase.multiply(BigDecimal.valueOf(0.18));

        // Round all
        brokerage = brokerage.setScale(2, RoundingMode.HALF_EVEN);
        etc = etc.setScale(2, RoundingMode.HALF_EVEN);
        stampDuty = stampDuty.setScale(2, RoundingMode.HALF_EVEN);
        ipf = ipf.setScale(2, RoundingMode.HALF_EVEN);
        sebiCharges = sebiCharges.setScale(2, RoundingMode.HALF_EVEN);
        stt = stt.setScale(2, RoundingMode.HALF_EVEN);
        gst = gst.setScale(2, RoundingMode.HALF_EVEN);

        // === Total Fee & Tax ===
        BigDecimal totalCharges = brokerage.add(etc).add(stampDuty).add(ipf).add(sebiCharges).add(stt).add(gst);
        BigDecimal backendTotal = backendSubtotal.add(totalCharges).setScale(2, RoundingMode.HALF_EVEN);

        // Cross check with frontend
        if (backendSubtotal.compareTo(dto.getSubtotal()) != 0) {
        	System.out.println("Subtotal mismatch: "+backendSubtotal+" : "+dto.getSubtotal());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, "Subtotal mismatch!"));
        }

        if (backendTotal.compareTo(dto.getTotalAmount()) != 0) {
        	System.out.println("Total mismatch: "+backendTotal+" : "+dto.getTotalAmount());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, "Total mismatch!"));
        }

        if (dto.getBalance().compareTo(backendTotal) < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, "Insufficient balance"));
        }

        // Update user balance
        user.setBalance(user.getBalance().subtract(backendTotal));
        userRepository.save(user);

        // Update stock IPO quantity
        stock.setIpoQty(stock.getIpoQty() - dto.getQuantity());
        stockRepository.save(stock);

        // Update or create holding
        Optional<HoldingEntity> optionalHolding = holdingRepository.findByUserAndStock(user, stock);
        if (optionalHolding.isPresent()) {
            HoldingEntity holding = optionalHolding.get();
            int oldQty = holding.getQuantity();
            BigDecimal oldAvg = holding.getAvgPrice();

            //new avg = ( oldAvg * oldQty ) + ( newPrice * qty) / oldQty + newQty
            BigDecimal newAvg = oldAvg.multiply(BigDecimal.valueOf(oldQty))
                    .add(pricePerUnit.multiply(quantity))
                    .divide(BigDecimal.valueOf(oldQty + dto.getQuantity()), 2, RoundingMode.HALF_EVEN);
            holding.setQuantity(oldQty + dto.getQuantity());
            holding.setAvgPrice(newAvg);
            holdingRepository.save(holding);
        } else {
            HoldingEntity holding = new HoldingEntity();
            holding.setUser(user);
            holding.setStock(stock);
            holding.setQuantity(dto.getQuantity());
            holding.setAvgPrice(pricePerUnit);
            holdingRepository.save(holding);
        }

     // Insert transaction
        TransactionEntity txn = new TransactionEntity();
        txn.setUser(user);
        txn.setStock(stock);
        txn.setTransactionType("BUY");
        txn.setQuantity(dto.getQuantity());
        txn.setPricePerUnit(pricePerUnit);
        txn.setSubTotal(backendSubtotal);

        // Set the new charges and taxes
        txn.setBrokerage(brokerage);
        txn.setExchangeTxnCharges(etc);
        txn.setStampDuty(stampDuty);
        txn.setIpft(ipf);
        txn.setSebiCharges(sebiCharges);
        txn.setStt(stt);
        txn.setGst(gst);

        // Total amount after adding all charges
        txn.setTotalAmount(backendTotal);

        // Save the transaction
        transactionRepository.save(txn);


        return ResponseEntity.ok(new SuccessResponse<>(200, "Stock purchased successfully"));
    }


    
    @Override
    @Transactional
    public ResponseEntity<?> sellStock(SellRequestDTO dto) {
        // 1. Check if user exists
        UserEntity user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found."));

        // 2. Check if stock exists
        StockEntity stock = stockRepository.findById(dto.getStockId())
                .orElseThrow(() -> new RuntimeException("Stock not found."));

        // 3. Check if user holds the stock
        HoldingEntity holding = holdingRepository.findByUserAndStock(user, stock)
                .orElseThrow(() -> new RuntimeException("User doesn't hold this stock"));

        // 4. Check if user has enough qty to sell
        if (holding.getQuantity() < dto.getQuantity()) {
            ErrorResponse responseBody = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Not Enough stock to sell.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        }

        // 5. Get current price as selling price
        //current market price is the selling price!
        BigDecimal currentPrice = stock.getCurrentPrice(); //get from market!
        BigDecimal subTotal = currentPrice.multiply(BigDecimal.valueOf(dto.getQuantity()));

        // === Charges Calculation ===

        // Brokerage: 0.25% of subtotal or max 20
        BigDecimal brokerage = subTotal.multiply(BigDecimal.valueOf(0.0025));
        if (brokerage.compareTo(BigDecimal.valueOf(20)) > 0) {
            brokerage = BigDecimal.valueOf(20);
        }

        // ETC: Based on exchange
        BigDecimal etc = stock.getExchange().equalsIgnoreCase("NSE")
                ? subTotal.multiply(BigDecimal.valueOf(0.0000325))
                : subTotal.multiply(BigDecimal.valueOf(0.00003));

        // Stamp Duty: 0 (not for sell!)
        BigDecimal stampDuty = BigDecimal.ZERO;

        // IPF: ₹0.0002 per share
        BigDecimal ipf = BigDecimal.valueOf(dto.getQuantity()).multiply(BigDecimal.valueOf(0.0002));

        // SEBI Charges: 0.001%
        BigDecimal sebiCharges = subTotal.multiply(BigDecimal.valueOf(0.00001));

        // STT: 0.025%
        BigDecimal stt = subTotal.multiply(BigDecimal.valueOf(0.00025));

        // GST: 18% on (brokerage + etc + sebi)
        BigDecimal gstBase = brokerage.add(etc).add(sebiCharges);
        BigDecimal gst = gstBase.multiply(BigDecimal.valueOf(0.18));

        // Round all
        brokerage = brokerage.setScale(2, RoundingMode.HALF_EVEN);
        etc = etc.setScale(2, RoundingMode.HALF_EVEN);
        ipf = ipf.setScale(2, RoundingMode.HALF_EVEN);
        sebiCharges = sebiCharges.setScale(2, RoundingMode.HALF_EVEN);
        stt = stt.setScale(2, RoundingMode.HALF_EVEN);
        gst = gst.setScale(2, RoundingMode.HALF_EVEN);

        // 6. Calculate total amount! (after adding all charges)
        System.out.println("Total before reducing charges: "+ subTotal);
        BigDecimal total = subTotal.subtract(brokerage).subtract(etc).subtract(stampDuty).subtract(ipf).subtract(sebiCharges).subtract(stt).subtract(gst);
        System.out.println("Total after reducing charges: "+ total);
        
        // 7. Update user balance
        user.setBalance(user.getBalance().add(total));
        userRepository.save(user);

        // 8. Update holdings
        if (holding.getQuantity() == dto.getQuantity()) {
            // User sells all the stocks!
            holdingRepository.delete(holding);
        } else {
            holding.setQuantity(holding.getQuantity() - dto.getQuantity());
            holdingRepository.save(holding);
        }

        // 9. Update IPO Quantity in stocks table
        stock.setIpoQty(stock.getIpoQty() + dto.getQuantity());
        stockRepository.save(stock);

        // 10. Save transaction
        TransactionEntity txn = new TransactionEntity();
        txn.setUser(user);
        txn.setStock(stock);
        txn.setTransactionType("SELL");
        txn.setQuantity(dto.getQuantity());
        txn.setPricePerUnit(stock.getCurrentPrice());
        txn.setSubTotal(subTotal);
        txn.setBrokerage(brokerage);
        txn.setExchangeTxnCharges(etc);
        txn.setStampDuty(stampDuty);
        txn.setIpft(ipf);
        txn.setSebiCharges(sebiCharges);
        txn.setStt(stt);
        txn.setGst(gst);
        txn.setTotalAmount(total);

        // Save the transaction
        transactionRepository.save(txn);

        SuccessResponse<Object> responseBody = new SuccessResponse<>(HttpStatus.OK.value(), "Stock sold successfully");
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    @Override
    public ResponseEntity<?> getUserHoldings(Long userId) {
        List<HoldingEntity> holdings = holdingRepository.findByUserId(userId);
        
        if(holdings.isEmpty()) {
        	SuccessResponse<Object> responseBody = new SuccessResponse<>(HttpStatus.OK.value(),"User does not hold any stock.",holdings);
        	 return ResponseEntity.status(HttpStatus.OK).body(responseBody);
        }
        
        List<HoldingResponseDTO> response = holdings.stream()
        		.map(holding -> {
        			HoldingResponseDTO dto = new HoldingResponseDTO();
        	        dto.setId(holding.getId());
        	        dto.setUserId(holding.getUser().getId());
        	        dto.setStockId(holding.getStock().getId());
        	        dto.setQuantity(holding.getQuantity());
        	        dto.setAvgPrice(holding.getAvgPrice());
        	        
        	        StockEntity stock = holding.getStock();
        	        StockInfoDTO stockDto = new StockInfoDTO();
        	        
        	        stockDto.setName(stock.getName());
        	        stockDto.setSymbol(stock.getSymbol());
        	        stockDto.setImageUrl(stock.getImageUrl());
        	        stockDto.setCurrentPrice(stock.getCurrentPrice());
        	        stockDto.setSector(stock.getSector());
        	        dto.setStock(stockDto);
        	        
        	        return dto;
        		}).collect(Collectors.toList());
        
        SuccessResponse<Object> responseBody = new SuccessResponse<>(HttpStatus.OK.value(), "Holdings fetched successfully", response);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    @Override
    public ResponseEntity<?> getUserTransactions(Long userId) {
        List<TransactionEntity> transactions = transactionRepository.findByUserId(userId);
        
        if(transactions.isEmpty()) {
        	SuccessResponse<Object> responseBody = new SuccessResponse<>(HttpStatus.OK.value(),"User has no recorded transactions.",transactions);
        	return ResponseEntity.status(HttpStatus.OK).body(responseBody);
        }
        
        List<TransactionResponseDTO> response = transactions.stream()
                .map(transaction -> {
                    TransactionResponseDTO dto = new TransactionResponseDTO();
                    dto.setId(transaction.getId());
                    dto.setTransactionType(transaction.getTransactionType());
                    dto.setQuantity(transaction.getQuantity());
                    dto.setPricePerUnit(transaction.getPricePerUnit());
                    dto.setSubTotal(transaction.getSubTotal());
                    dto.setTotalAmount(transaction.getTotalAmount());
                    dto.setTimestamp(transaction.getTimestamp());
                    
                    //charges info
                    dto.setBrokerage(transaction.getBrokerage());
                    dto.setExchangeTxnCharges(transaction.getExchangeTxnCharges());
                    dto.setStampDuty(transaction.getStampDuty());
                    dto.setIpft(transaction.getIpft());
                    dto.setSebiCharges(transaction.getSebiCharges());
                    dto.setStt(transaction.getStt());
                    dto.setGst(transaction.getGst());
                    
                    // Get stock info
                    StockEntity stock = transaction.getStock();
                    StockInfoDTO stockDto = new StockInfoDTO();
                    
                    stockDto.setName(stock.getName());
                    stockDto.setSymbol(stock.getSymbol());
                    stockDto.setImageUrl(stock.getImageUrl());
                    stockDto.setCurrentPrice(stock.getCurrentPrice());
                    stockDto.setSector(stock.getSector());
                    
                    // Set stock info in DTO
                    dto.setStock(stockDto);
                    
                    return dto;
                })
                .collect(Collectors.toList());
        
        SuccessResponse<Object> responseBody = new SuccessResponse<>(HttpStatus.OK.value(), "Transactions fetched successfully", response);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }
    
    @Override
    public ResponseEntity<?> getUserPortfolio(Long userId){
    	
    	SuccessResponse<Object> responseBody = new SuccessResponse<>(HttpStatus.OK.value(),"Portfolio fetched successfully");
    	return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

}
