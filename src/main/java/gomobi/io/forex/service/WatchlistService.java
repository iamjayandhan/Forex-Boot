package gomobi.io.forex.service;

import java.util.List;
import gomobi.io.forex.entity.StockEntity;
import gomobi.io.forex.entity.Watchlist;

public interface WatchlistService {
    
    List<StockEntity> getUserWatchlist(Long userId);

    Watchlist addToWatchlist(Long userId, Long stockId);

    void removeFromWatchlist(Long userId, Long stockId);
}
