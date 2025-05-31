package gomobi.io.forex.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gomobi.io.forex.entity.TransactionEntity;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
	
	//pagination version
	Page<TransactionEntity> findByUserId(Long userId,Pageable pageable);
	
	//pagination + transaction type(BUY,SELL,ALL)
	Page<TransactionEntity> findByUserIdAndTransactionType(Long userId, String transactionType, Pageable pageable);
	
	//pagination + transaction type + date filter!
	Page<TransactionEntity> findByUserIdAndTransactionTypeAndTimestampBetween(
		    Long userId, String transactionType, LocalDateTime start, LocalDateTime end, Pageable pageable);

	//like the the above, but no transaction type
	Page<TransactionEntity> findByUserIdAndTimestampBetween(
		    Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);
}
