package gomobi.io.forex.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import gomobi.io.forex.dto.SuccessResponse;
import gomobi.io.forex.dto.WalletTransactionRequestDTO;
import gomobi.io.forex.entity.UserEntity;
import gomobi.io.forex.entity.WalletTransactionEntity;
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
	public ResponseEntity<?> saveTransaction(WalletTransactionRequestDTO requestDTO) {
		
		Optional<UserEntity> optionalUser = userRepository.findById(requestDTO.getUserId());
		UserEntity user = optionalUser.get();	
	
		WalletTransactionEntity transaction = new WalletTransactionEntity();
		transaction.setUser(user);
		transaction.setAmount(requestDTO.getAmount());
		transaction.setBalance(requestDTO.getBalance()); //new balance
		transaction.setTransactionType(requestDTO.getTransactionType());
		walletTransactionRepository.save(transaction);
		
		SuccessResponse<Object> responseBody = new SuccessResponse<>(HttpStatus.ACCEPTED.value(),"Wallet transaction made successfully.");
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseBody);
	}
	
	@Override
	public List<WalletTransactionEntity> getTransactionsByUserId(Long userId){

        List<WalletTransactionEntity> transactions = walletTransactionRepository.findByUserId(userId);
        return transactions;
	}
	
	//to find the whole deposit amount!
//	@Override
//	public BigDecimal getTotalAmountByUser(UserEntity user) {
//		return walletTransactionRepository.findByUser(user).stream()
//				.map(tx -> tx.getTransactionType().name().equals("DEPOSIT") ? tx.getAmount() : tx.getAmount().negate())
//				.reduce(BigDecimal.ZERO, BigDecimal::add);
//	}
}
