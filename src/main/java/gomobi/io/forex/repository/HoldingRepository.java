package gomobi.io.forex.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gomobi.io.forex.entity.HoldingEntity;
import gomobi.io.forex.entity.StockEntity;
import gomobi.io.forex.entity.UserEntity;

@Repository
public interface HoldingRepository extends JpaRepository<HoldingEntity, Long> {
    List<HoldingEntity> findByUserId(Long userId);
    Optional<HoldingEntity> findByUserAndStock(UserEntity user, StockEntity stock);

    Page<HoldingEntity> findByUserId(Long userId,Pageable pageable);
    
    boolean existsByStockId(Long stockId);
}
