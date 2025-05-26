package gomobi.io.forex.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gomobi.io.forex.entity.UserEntity;
import gomobi.io.forex.entity.WalletTransactionEntity;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransactionEntity, Long>{
	
	//get all at once
	List<WalletTransactionEntity> findByUserId(Long userId);
	
	//paginated
	Page<WalletTransactionEntity> findByUser(UserEntity user, Pageable pageable);
}
