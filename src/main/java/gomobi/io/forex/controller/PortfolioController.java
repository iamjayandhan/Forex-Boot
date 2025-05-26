package gomobi.io.forex.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gomobi.io.forex.dto.BuyRequestDTO;
import gomobi.io.forex.dto.HoldingResponseDTO;
import gomobi.io.forex.dto.PageResponse;
import gomobi.io.forex.dto.SellRequestDTO;
import gomobi.io.forex.dto.StockInfoDTO;
import gomobi.io.forex.dto.SuccessResponse;
import gomobi.io.forex.dto.WalletTransactionDTO;
import gomobi.io.forex.entity.HoldingEntity;
import gomobi.io.forex.entity.WalletTransactionEntity;
import gomobi.io.forex.exception.ErrorResponse;
import gomobi.io.forex.repository.UserRepository;
import gomobi.io.forex.service.HoldingService;
import gomobi.io.forex.service.PortfolioService;
import gomobi.io.forex.service.WalletTransactionService;



@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;
    
    @Autowired
    private HoldingService holdingService;
    
    @Autowired
    private WalletTransactionService walletTransactionService;
        
    private final UserRepository userRepository;
    
    @Autowired
    public PortfolioController(UserRepository userRepository) {
    	this.userRepository = userRepository;
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
    //get all at once!
    @GetMapping("/holdings/{userId}")
    public ResponseEntity<?> getUserHoldings(@PathVariable Long userId) {
        return portfolioService.getUserHoldings(userId);
    }
    
    //Get Holdings (Paginated)
    @GetMapping("/holdings/paginated")
    public ResponseEntity<?> getPaginatedHoldings(@RequestParam int page,
                                                  @RequestParam int size,
                                                  @RequestParam(required = false) Long userId) {
        Page<HoldingEntity> holdingsPage;

        if (userId != null) {
            if (!userRepository.existsById(userId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            //get all user holdings
            holdingsPage = holdingService.getPaginatedHoldingsByUserId(userId, page, size);
        } else {
        	//get all holdings
            holdingsPage = holdingService.getPaginatedHoldings(page, size);
        }

        // Map HoldingEntity list to HoldingResponseDTO list
        List<HoldingResponseDTO> dtoList = holdingsPage.getContent().stream()
            .map(holding -> {
                HoldingResponseDTO dto = new HoldingResponseDTO();
                dto.setId(holding.getId());
                dto.setUserId(holding.getUser().getId());  // remove if you don't want userId in response
                dto.setStockId(holding.getStock().getId());
                dto.setQuantity(holding.getQuantity());
                dto.setAvgPrice(holding.getAvgPrice());

                StockInfoDTO stockDto = new StockInfoDTO();
                stockDto.setName(holding.getStock().getName());
                stockDto.setSymbol(holding.getStock().getSymbol());
                stockDto.setImageUrl(holding.getStock().getImageUrl());
                stockDto.setCurrentPrice(holding.getStock().getCurrentPrice());
                stockDto.setSector(holding.getStock().getSector());

                dto.setStock(stockDto);

                return dto;
            }).collect(Collectors.toList());

   
        PageResponse<HoldingResponseDTO> pageResponse = new PageResponse<>(dtoList, holdingsPage.getTotalElements(),holdingsPage.getNumber(),holdingsPage.getSize());
        SuccessResponse<PageResponse<HoldingResponseDTO>> responseBody = new SuccessResponse<>(HttpStatus.OK.value(), "Holdings fetched successfully!", pageResponse);

        return ResponseEntity.ok(responseBody);
    }

    //Get Transactions
    @GetMapping("/transactions/{userId}")
    public ResponseEntity<?> getUserTransactions(@PathVariable Long userId) {
        return portfolioService.getUserTransactions(userId);
    }
    
    //Get paginated transactions
    @GetMapping("/transactions/paginated")
    public ResponseEntity<?> getUserTransactionsPaginated(
    		@RequestParam int page,
    		@RequestParam int size,
    		@RequestParam Long userId){
    	 return portfolioService.getUserTransactionsPaginated(userId,page,size);
    }
    

    //for the wallet transactions
    //save a wallet transaction!
    @PostMapping("/wallet")
    public ResponseEntity<?> saveWalletTransaction(@RequestBody WalletTransactionDTO requestDTO){
    	//validate user
    	if (!userRepository.existsById(requestDTO.getUserId())) {
    		ErrorResponse responseBody = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),"User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
        }
    	
    	//validate amount
    	if(requestDTO.getAmount().compareTo(BigDecimal.valueOf(10000)) > 0 ) {
    		ErrorResponse responseBody = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),"Requested amount exceeds the max transaction limit.");
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    	}
    	
    	if(requestDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
    		ErrorResponse responseBody = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),"Requested amount cannot be less than or equal to ZERO. Please provide valid ");
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    	}
    	
    	return walletTransactionService.saveTransaction(requestDTO);
    }
    
    //get wallet transactions of a user.
    @GetMapping("/wallet/{userId}")
    public ResponseEntity<?> getTransactionsByUser(@PathVariable Long userId) {
    	
    	//validate user
    	if (!userRepository.existsById(userId)) {
    		ErrorResponse responseBody = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),"User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
        }
    	
        List<WalletTransactionEntity> transactions = walletTransactionService.getTransactionsByUserId(userId);
        
        List<WalletTransactionDTO> dtoList = transactions.stream()
        		.map(txn -> new WalletTransactionDTO(
        				txn.getId(),
        				txn.getUser().getId(),
        				txn.getTransactionType(),
        				txn.getTransactionReason(),
        				txn.getAmount(),
        				txn.getBalance(),
        				txn.getTimestamp()
        		)).collect(Collectors.toList());
        		
        
        SuccessResponse<Object> responseBody = new SuccessResponse<>(HttpStatus.OK.value(),"Wallet Transactions fetched successfully.",dtoList);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseBody);
    }
    
    @GetMapping("/wallet/paginated")
    public ResponseEntity<?> getWalletTransactionsByUser(@RequestParam int page,
	            @RequestParam int size,
	            @RequestParam Long userId){
    	
    	return walletTransactionService.getWalletTransactionsPaginated(userId, page, size);
    }

}
