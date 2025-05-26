package gomobi.io.forex.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SellRequestDTO {
	private String email;
	private Long stockId;
	private int quantity;
	
	//constructors
	public SellRequestDTO(String email,Long stockId, int quantity) {
		this.email = email;
		this.stockId = stockId;
		this.quantity = quantity;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public void setEmail(String email) {
		this.email = email;
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
}
