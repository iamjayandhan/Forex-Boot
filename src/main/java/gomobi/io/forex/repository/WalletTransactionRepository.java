package gomobi.io.forex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gomobi.io.forex.entity.WalletTransactionEntity;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransactionEntity, Long>{
	List<WalletTransactionEntity> findByUserId(Long userId);
}
