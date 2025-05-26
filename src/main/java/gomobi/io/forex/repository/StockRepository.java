package gomobi.io.forex.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gomobi.io.forex.entity.StockEntity;

@Repository
public interface StockRepository extends JpaRepository<StockEntity, Long> {
    Page<StockEntity> findByNameContaining(String name, Pageable pageable);
    Page<StockEntity> findAll(Pageable pageable);
    
    @Query("SELECT s FROM StockEntity s WHERE " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.symbol) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.exchange) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.sector) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.description) LIKE LOWER(CONCAT('%', :search, '%'))")
     Page<StockEntity> searchAllFields(@Param("search") String search, Pageable pageable);
}

