package gomobi.io.forex.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import gomobi.io.forex.dto.WalletTransactionRequestDTO;
import gomobi.io.forex.entity.WalletTransactionEntity;

public interface WalletTransactionService {
	ResponseEntity<?> saveTransaction(WalletTransactionRequestDTO requestDTO);
	List<WalletTransactionEntity> getTransactionsByUserId(Long userId);
	
	//to get whole deposit sum of the user!
	//BigDecimal getTotalAmountByUser(UserEntity user);
}
