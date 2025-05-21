package gomobi.io.forex.service;

import java.util.List;

import java.util.Optional;

import org.springframework.data.domain.Page;

import gomobi.io.forex.dto.StockDTO;
import gomobi.io.forex.dto.UpdateStockDTO;
import gomobi.io.forex.entity.StockEntity;

public interface StockService {

    StockEntity createStockFromDto(StockDTO stockDTO);
    List<StockEntity> getAllStocks();
    
    Optional<StockEntity> getStockById(Long id);
    StockEntity partialUpdateStock(Long id,UpdateStockDTO dto);
    void deleteStock(Long id);
    
    Page<StockEntity> getPaginatedStocks(int page,int size,String search);
 }
