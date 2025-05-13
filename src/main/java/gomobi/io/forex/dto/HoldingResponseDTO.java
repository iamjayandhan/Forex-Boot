package gomobi.io.forex.dto;

import java.math.BigDecimal;

public class HoldingResponseDTO {
	private Long id;
	private Long userId;
	private Long stockId;
	private int quantity;
	private BigDecimal avgPrice;
	private StockInfoDTO stock;
	
	//getters and setters
	public Long getId() {
		return this.id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getUserId() {
		return this.userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public Long getStockId() {
		return this.stockId;
	}
	
	public void setStockId(Long stockId) {
		this.stockId = stockId;
	}
	
	public int getQuantity() {
		return this.quantity;
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public BigDecimal getAvgPrice() {
		return this.avgPrice;
	}
	
	public void setAvgPrice(BigDecimal avgPrice) {
		this.avgPrice = avgPrice;
	}
	
	public StockInfoDTO getStock() {
		return this.stock;
	}
	
	public void setStock(StockInfoDTO stock) {
		this.stock = stock;
	}
}
