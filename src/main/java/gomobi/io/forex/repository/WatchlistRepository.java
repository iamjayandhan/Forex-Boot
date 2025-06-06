package gomobi.io.forex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gomobi.io.forex.entity.Watchlist;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
    List<Watchlist> findByUserId(Long userId);
    void deleteByUserIdAndStockId(Long userId, Long stockId);
    
    boolean existsByUserId(Long userId);
    boolean existsByUserIdAndStockId(Long userId, Long stockId);
}
