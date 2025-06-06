package gomobi.io.forex.repository;

import java.util.List;

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
    
    //check all the fields
    @Query("SELECT s FROM StockEntity s WHERE " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.symbol) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.exchange) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.sector) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<StockEntity> searchAllFields(@Param("search") String search, Pageable pageable);
    Page<StockEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    @Query("SELECT s FROM StockEntity s WHERE " +
            "(:search IS NULL OR :search = '' OR " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.symbol) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.exchange) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.sector) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "(:sector IS NULL OR :sector = '' OR LOWER(s.sector) = LOWER(:sector)) AND " +
            "(:exchange IS NULL OR :exchange = '' OR LOWER(s.exchange) = LOWER(:exchange))")
    Page<StockEntity> searchWithFilters(@Param("search") String search,
                                        @Param("sector") String sector,
                                        @Param("exchange") String exchange,
                                        Pageable pageable);
    
    @Query("SELECT DISTINCT s.sector FROM StockEntity s WHERE s.sector IS NOT NULL")
    List<String> findAllDistinctSectors();
    
    //get all stock info via list of its id's
    List<StockEntity> findByIdIn(List<Long> ids);
}

