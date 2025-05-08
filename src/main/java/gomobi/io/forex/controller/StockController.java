package gomobi.io.forex.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gomobi.io.forex.dto.StockDTO;
import gomobi.io.forex.dto.SuccessResponse;
import gomobi.io.forex.entity.StockEntity;
import gomobi.io.forex.exception.ErrorResponse;
import gomobi.io.forex.service.StockService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping
    public ResponseEntity<?> createStock(@Valid @RequestBody StockDTO stockDTO, BindingResult result) {
        if (result.hasErrors()) {
            return handleValidationErrors(result);
        }

        // Use the service to create stock from DTO
        StockEntity savedStock = stockService.createStockFromDto(stockDTO);
        
        SuccessResponse<StockEntity> responseBody = new SuccessResponse<>(HttpStatus.CREATED.value(), "Stock created successfully", savedStock);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStockById(@PathVariable Long id) {
        Optional<StockEntity> stock = stockService.getStockById(id);
        if (stock.isPresent()) {
            return ResponseEntity.ok(new SuccessResponse<>(HttpStatus.OK.value(), "Stock retrieved successfully!", stock.get()));
        } else {
            ErrorResponse responseBody = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Stock not found with the ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStock(@PathVariable Long id,
                                         @Valid @RequestBody StockDTO stockDTO, BindingResult result) {
        if (result.hasErrors()) {
            return handleValidationErrors(result);
        }
        
        // Use the service to update stock from DTO
        StockEntity updatedStock = stockService.updateStockFromDto(id, stockDTO);
        SuccessResponse<Object> responseBody = new SuccessResponse<>(HttpStatus.OK.value(), "Stock updated successfully", updatedStock);
        return ResponseEntity.ok(responseBody);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStock(@PathVariable Long id) {
        stockService.deleteStock(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();  // Standard No Content response
    }

    // Utility method for handling validation errors
    private ResponseEntity<ErrorResponse> handleValidationErrors(BindingResult result) {
        String errorMessage = result.getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        ErrorResponse responseBody = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }
}
