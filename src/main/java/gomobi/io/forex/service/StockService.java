package gomobi.io.forex.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import gomobi.io.forex.dto.StockDTO;
import gomobi.io.forex.dto.UpdateStockDTO;
import gomobi.io.forex.entity.StockEntity;

public interface StockService {

    StockEntity createStockFromDto(StockDTO stockDTO);
    List<StockEntity> getAllStocks();
    List<String> getAllSectors();
    
    Optional<StockEntity> getStockById(Long id);
    StockEntity partialUpdateStock(Long id,UpdateStockDTO dto);
    void deleteStock(Long id);
    
    Page<StockEntity> getPaginatedStocks(Pageable pageable,String search,String sector,String exchange);
 }
