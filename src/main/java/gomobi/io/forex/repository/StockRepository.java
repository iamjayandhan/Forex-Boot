package gomobi.io.forex.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gomobi.io.forex.entity.StockEntity;

@Repository
public interface StockRepository extends JpaRepository<StockEntity, Long> {
    Page<StockEntity> findByNameContaining(String name, Pageable pageable);
    Page<StockEntity> findAll(Pageable pageable);
}

