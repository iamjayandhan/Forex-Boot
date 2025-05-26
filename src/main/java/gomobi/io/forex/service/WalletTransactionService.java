package gomobi.io.forex.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import gomobi.io.forex.dto.WalletTransactionDTO;
import gomobi.io.forex.entity.WalletTransactionEntity;

public interface WalletTransactionService {
	ResponseEntity<?> saveTransaction(WalletTransactionDTO requestDTO);
	List<WalletTransactionEntity> getTransactionsByUserId(Long userId);
	
	//paginated
	ResponseEntity<?> getWalletTransactionsPaginated(Long userId, int page, int size);
}
