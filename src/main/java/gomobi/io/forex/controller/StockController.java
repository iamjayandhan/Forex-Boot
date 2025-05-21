package gomobi.io.forex.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gomobi.io.forex.dto.PageResponse;
import gomobi.io.forex.dto.StockDTO;
import gomobi.io.forex.dto.SuccessResponse;
import gomobi.io.forex.dto.UpdateStockDTO;
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
    
    @GetMapping
    public ResponseEntity<?> getAllStocks(){
    	List<StockEntity> stocks = stockService.getAllStocks();
    	SuccessResponse<Object> responseBody = new SuccessResponse<>(HttpStatus.OK.value(),"All Stocks are fetched successfully!",stocks);
    	return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }
    
    @GetMapping("/paginated")
    public ResponseEntity<?> getPaginatedStocks(@RequestParam int page, @RequestParam int size, @RequestParam(required = false) String search) {
        // Fetch stocks with pagination, this is an example, you can replace with your actual service logic
        Page<StockEntity> stockPage = stockService.getPaginatedStocks(page, size, search);

        // Wrap the content and total elements inside a PageResponse
        PageResponse<StockEntity> pageResponse = new PageResponse<>(stockPage.getContent(), stockPage.getTotalElements());

        // Return the paginated response inside a SuccessResponse
        SuccessResponse<PageResponse<StockEntity>> responseBody = new SuccessResponse<>(HttpStatus.OK.value(), "Stocks fetched successfully!", pageResponse);

        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
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
                                         @Valid @RequestBody UpdateStockDTO stockDTO) {
        // Use the service to update stock from DTO
        StockEntity updatedStock = stockService.partialUpdateStock(id, stockDTO);
        SuccessResponse<Object> responseBody = new SuccessResponse<>(HttpStatus.OK.value(), "Stock updated successfully", updatedStock);
        return ResponseEntity.ok(responseBody);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStock(@PathVariable Long id) {
    	
    	Optional<StockEntity> stockOptional = stockService.getStockById(id);
    	
    	if(stockOptional.isPresent()) {
    		stockService.deleteStock(id);
    		
    		SuccessResponse<Object> responseBody = new SuccessResponse<>(HttpStatus.OK.value(),"Deletion operation successful.");
    		return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    	}
    	else {
    		ErrorResponse responseBody = new ErrorResponse(HttpStatus.NOT_FOUND.value(),"Stock with ID "+ id + " not found");
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
    	}
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
