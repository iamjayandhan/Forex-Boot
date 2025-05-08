package gomobi.io.forex.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gomobi.io.forex.dto.StockDTO;
import gomobi.io.forex.entity.StockEntity;
import gomobi.io.forex.repository.StockRepository;
import gomobi.io.forex.service.StockService;

@Service
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    @Autowired
    public StockServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    public StockEntity createStock(StockEntity stock) {
        return stockRepository.save(stock);
    }

    @Override
    public List<StockEntity> getAllStocks() {
        return stockRepository.findAll();
    }

    @Override
    public Optional<StockEntity> getStockById(Long id) {
        return stockRepository.findById(id);
    }

    @Override
    public StockEntity updateStock(Long id, StockEntity stockDetails) {
        StockEntity stock = stockRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Stock not found"));

        stock.setName(stockDetails.getName());
        stock.setSymbol(stockDetails.getSymbol());
        stock.setImageUrl(stockDetails.getImageUrl());
        stock.setCurrentPrice(stockDetails.getCurrentPrice());
        stock.setSector(stockDetails.getSector());
        stock.setDescription(stockDetails.getDescription());
        stock.setIpoQty(stockDetails.getIpoQty());
        stock.setExchange(stockDetails.getExchange());

        return stockRepository.save(stock);
    }

    @Override
    public void deleteStock(Long id) {
        stockRepository.deleteById(id);
    }

    // Method to create a stock from DTO
    @Override
    public StockEntity createStockFromDto(StockDTO stockDTO) {
        StockEntity stockEntity = mapDtoToEntity(stockDTO);  // Convert DTO to Entity
        return stockRepository.save(stockEntity);  // Save entity to the database
    }

    // Method to update stock from DTO
    @Override
    public StockEntity updateStockFromDto(Long id, StockDTO stockDTO) {
        StockEntity stock = stockRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Stock not found"));

        // Convert DTO to Entity and update
        StockEntity updatedStock = mapDtoToEntity(stockDTO);
        updatedStock.setId(id);  // Ensure the ID remains unchanged during update

        return stockRepository.save(updatedStock);  // Save the updated entity
    }

    // Helper method to convert DTO to Entity
    private StockEntity mapDtoToEntity(StockDTO stockDTO) {
        StockEntity entity = new StockEntity();
        entity.setName(stockDTO.getName());
        entity.setSymbol(stockDTO.getSymbol());
        entity.setImageUrl(stockDTO.getImageUrl());
        entity.setCurrentPrice(stockDTO.getCurrentPrice());
        entity.setSector(stockDTO.getSector());
        entity.setDescription(stockDTO.getDescription());
        entity.setIpoQty(stockDTO.getIpoQty());
        entity.setExchange(stockDTO.getExchange());
        return entity;
    }
}
