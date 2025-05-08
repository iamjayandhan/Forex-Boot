package gomobi.io.forex.service;

import java.util.List;
import java.util.Optional;

import gomobi.io.forex.dto.StockDTO;
import gomobi.io.forex.entity.StockEntity;

public interface StockService {

    StockEntity createStock(StockEntity stock);
    StockEntity updateStockFromDto(Long id,StockDTO stockDTO);
    StockEntity createStockFromDto(StockDTO stockDTO);
    
    List<StockEntity> getAllStocks();
    Optional<StockEntity> getStockById(Long id);
    
    StockEntity updateStock(Long id, StockEntity stockDetails);
    void deleteStock(Long id);
}
