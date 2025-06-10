package gomobi.io.forex.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import gomobi.io.forex.dto.SuccessResponse;
import gomobi.io.forex.dto.WalletTransactionDTO;
import gomobi.io.forex.entity.UserEntity;
import gomobi.io.forex.entity.WalletTransactionEntity;
import gomobi.io.forex.enums.StatusEnum;
import gomobi.io.forex.repository.UserRepository;
import gomobi.io.forex.repository.WalletTransactionRepository;
import gomobi.io.forex.service.WalletTransactionService;

@Service
public class WalletTransactionServiceImpl implements WalletTransactionService{
	
	private final WalletTransactionRepository walletTransactionRepository;
	
	private final UserRepository userRepository;
	
	
	@Autowired
	public WalletTransactionServiceImpl(WalletTransactionRepository walletTransactionRepository,UserRepository userRepository) {
		this.walletTransactionRepository = walletTransactionRepository;
		this.userRepository = userRepository;
	}
	
	@Override
	public ResponseEntity<?> saveTransaction(WalletTransactionDTO requestDTO) {
		
		Optional<UserEntity> optionalUser = userRepository.findById(requestDTO.getUserId());
		UserEntity user = optionalUser.get();	
	
		WalletTransactionEntity transaction = new WalletTransactionEntity();
		transaction.setUser(user);
		transaction.setAmount(requestDTO.getAmount());
		transaction.setBalance(requestDTO.getBalance()); //new balance
		transaction.setTransactionType(requestDTO.getTransactionType());
		transaction.setTransactionReason(requestDTO.getTransactionReason());
		walletTransactionRepository.save(transaction);
		
		SuccessResponse<Object> responseBody = new SuccessResponse<>(HttpStatus.ACCEPTED.value(),"Wallet transaction made successfully.");
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseBody);
	}
	
	//get all wallet transactions
	@Override
	public List<WalletTransactionEntity> getTransactionsByUserId(Long userId){

        List<WalletTransactionEntity> transactions = walletTransactionRepository.findByUserIdAndStatus(userId,StatusEnum.SUCCESS);
        return transactions;
	}
	
	//get wallet transactions paginated
	 public ResponseEntity<?> getWalletTransactionsPaginated(Long userId, int page, int size) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Pageable pageable = PageRequest.of(page, size);
        Page<WalletTransactionEntity> results =  walletTransactionRepository.findByUserAndStatus(user, pageable,StatusEnum.SUCCESS);
                
        List<WalletTransactionDTO> transactionDTOs = results.getContent().stream()
                .map(entity -> new WalletTransactionDTO(
                    entity.getId(),
                    entity.getTransactionType(),
                    entity.getTransactionReason(),
                    entity.getAmount(),
                    entity.getBalance(),
                    entity.getTimestamp()
                ))
                .toList();

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("transactions", transactionDTOs);
            responseData.put("currentPage", results.getNumber());
            responseData.put("totalPages", results.getTotalPages());
            responseData.put("totalItems", results.getTotalElements());
        SuccessResponse<Object> requestBody = new SuccessResponse<>(HttpStatus.OK.value(),"User wallet transactions fetched successfully",responseData);
        return ResponseEntity.status(HttpStatus.OK).body(requestBody);
	}
}
