package gomobi.io.forex.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gomobi.io.forex.entity.StockEntity;
import gomobi.io.forex.entity.Watchlist;
import gomobi.io.forex.repository.StockRepository;
import gomobi.io.forex.repository.WatchlistRepository;

@Service
public class WatchlistService {

    @Autowired
    private WatchlistRepository watchlistRepository;
    
    @Autowired
    private StockRepository stockRepository;

    public List<StockEntity> getUserWatchlist(Long userId) {
        List<Watchlist> watchlistItems =  watchlistRepository.findByUserId(userId);
        List<Long> stockIds = watchlistItems.stream()
        		.map(Watchlist:: getStockId)
        		.collect(Collectors.toList());
        
        return stockRepository.findByIdIn(stockIds);
    }
    
    @Transactional
    public Watchlist addToWatchlist(Long userId, Long stockId) {
        if (!watchlistRepository.existsByUserIdAndStockId(userId, stockId)) {
            Watchlist entry = new Watchlist(userId, stockId);
            return watchlistRepository.save(entry);
        }
        throw new IllegalStateException("Already in watchlist");
    }

    @Transactional
    public void removeFromWatchlist(Long userId, Long stockId) {
        watchlistRepository.deleteByUserIdAndStockId(userId, stockId);
    }
}
