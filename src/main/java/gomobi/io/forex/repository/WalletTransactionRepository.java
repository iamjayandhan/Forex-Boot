package gomobi.io.forex.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gomobi.io.forex.entity.TransactionEntity;
import gomobi.io.forex.entity.UserEntity;
import gomobi.io.forex.entity.WalletTransactionEntity;
import gomobi.io.forex.enums.StatusEnum;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransactionEntity, Long>{
	
	//for downloadable report
	List<WalletTransactionEntity> findByUserId(Long userId);
	
	//get all at once
	List<WalletTransactionEntity> findByUserIdAndStatus(Long userId,StatusEnum status);
	
	//paginated
	Page<WalletTransactionEntity> findByUserAndStatus(UserEntity user, Pageable pageable, StatusEnum status);
	
	Optional<WalletTransactionEntity> findFirstByUserIdAndStatusOrderByTimestampDesc(
		    Long userId,
		    StatusEnum status);
}
