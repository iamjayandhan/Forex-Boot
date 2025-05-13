package gomobi.io.forex.repository;

import gomobi.io.forex.entity.HoldingEntity;
import gomobi.io.forex.entity.StockEntity;
import gomobi.io.forex.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HoldingRepository extends JpaRepository<HoldingEntity, Long> {
    List<HoldingEntity> findByUserId(Long userId);
    Optional<HoldingEntity> findByUserAndStock(UserEntity user, StockEntity stock);
}
