package gomobi.io.forex.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gomobi.io.forex.entity.TransactionEntity;
import gomobi.io.forex.enums.StatusEnum;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
	
	//for downloadable report
	List<TransactionEntity> findByUserId(Long userId);
	
	//pagination version
	Page<TransactionEntity> findByUserIdAndStatus(Long userId,Pageable pageable,StatusEnum status);
	
	//pagination + transaction type(BUY,SELL,ALL)
	Page<TransactionEntity> findByUserIdAndTransactionTypeAndStatus(Long userId, String transactionType, Pageable pageable,StatusEnum status);
	
	//pagination + transaction type + date filter!
	Page<TransactionEntity> findByUserIdAndTransactionTypeAndStatusAndTimestampBetween(
		    Long userId, String transactionType, LocalDateTime start, LocalDateTime end, Pageable pageable,StatusEnum status);

	//like the the above, but no transaction type
	Page<TransactionEntity> findByUserIdAndStatusAndTimestampBetween(
		    Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable,StatusEnum status);
	
	Optional<TransactionEntity> findFirstByUserIdAndStockIdAndStatusOrderByTimestampDesc(
		    Long userId,
		    Long stockId,
		    StatusEnum status);	
}
