package gomobi.io.forex.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StockInfoDTO {
	private String name;
	private String symbol;
	private String imageUrl;
	private BigDecimal currentPrice;
	private String sector;
	
	//getters and setters nanba!
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSymbol() {
		return this.symbol;
	}
	
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	public String getImageUrl() {
		return this.imageUrl;
	}
	
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public BigDecimal getCurrentPrice() {
		return this.currentPrice;
	}
	
	public void setCurrentPrice(BigDecimal currentPrice) {
		this.currentPrice = currentPrice;
	}
	
	public String getSector() {
		return this.sector;
	}
	
	public void setSector(String sector) {
		this.sector = sector;
	}
}
