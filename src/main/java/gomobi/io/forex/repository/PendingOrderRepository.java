package gomobi.io.forex.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gomobi.io.forex.entity.PendingOrderEntity;

@Repository
public interface PendingOrderRepository extends JpaRepository<PendingOrderEntity, Long> {
	Optional<PendingOrderEntity> findFirstByUserIdAndStockIdAndQuantityOrderByCreatedAtDesc(Long userId, Long stockId, Integer quantity);
	void deleteByUserIdAndStockId(Long userId, Long stockId);
}
