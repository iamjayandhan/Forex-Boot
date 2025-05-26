package gomobi.io.forex.service.impl;

import java.util.List;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import gomobi.io.forex.dto.StockDTO;
import gomobi.io.forex.dto.UpdateStockDTO;
import gomobi.io.forex.entity.StockEntity;
import gomobi.io.forex.exception.ResourceNotFoundException;
import gomobi.io.forex.repository.StockRepository;
import gomobi.io.forex.service.StockService;

@Service
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
//    private final HoldingRepository holdingRepository;

    @Autowired
    public StockServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    //first i check with stockDTO, if ok i will convert that into entity form and save it to DB.
    // Method to create a stock from DTO
    @Override
    public StockEntity createStockFromDto(StockDTO stockDTO) {
    	StockEntity stockEntity = mapDtoToEntity(stockDTO);  // Convert DTO to Entity
    	return stockRepository.save(stockEntity); 
    }
    
    @Override
    public List<StockEntity> getAllStocks() {
        return stockRepository.findAll();
    }

    @Override
    public Page<StockEntity> getPaginatedStocks(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size); //pageable obj!

        if (search != null && !search.isEmpty()) {
//            return stockRepository.findByNameContaining(search, pageable);
        	return stockRepository.searchAllFields(search, pageable);
        } else {
            return stockRepository.findAll(pageable); 
        }
    }
    
//    @Override
//    public Page<HoldingEntity> getPaginatedHoldingsStocks(int page, int size, String search) {
//        Pageable pageable = PageRequest.of(page, size); //pageable obj!
//
//        if (search != null && !search.isEmpty()) {
//            return holdingRepository.findByNameContaining(search, pageable);
//        } else {
//            return holdingRepository.findAll(pageable); 
//        }
//    }

    @Override
    public Optional<StockEntity> getStockById(Long id) {
        return stockRepository.findById(id);
    }

    @Override
    public StockEntity partialUpdateStock(Long id, UpdateStockDTO dto) {
        StockEntity stock = stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found"));

        if (dto.getName() != null) stock.setName(dto.getName());
        if (dto.getSymbol() != null) stock.setSymbol(dto.getSymbol());
        if (dto.getImageUrl() != null) stock.setImageUrl(dto.getImageUrl());
        if (dto.getCurrentPrice() != null) stock.setCurrentPrice(dto.getCurrentPrice());
        if (dto.getSector() != null) stock.setSector(dto.getSector());
        if (dto.getDescription() != null) stock.setDescription(dto.getDescription());
        if (dto.getIpoQty() != null) stock.setIpoQty(dto.getIpoQty());
        if (dto.getExchange() != null) stock.setExchange(dto.getExchange());

        return stockRepository.save(stock);
    }

    @Override
    public void deleteStock(Long id) {
        stockRepository.deleteById(id);
    }

    // convert DTO to Entity - this is for create stock
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
