package gomobi.io.forex.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuyRequestDTO {
    private String email;             
    private Long stockId;             
    private int quantity;             
    private BigDecimal balance;
    private BigDecimal subtotal;
    private BigDecimal totalAmount;
    private String transactionId;

    public BuyRequestDTO(String email, Long stockId, int quantity,
                          BigDecimal subtotal, BigDecimal totalAmount, BigDecimal avgPrice,String transactionId) {
        this.email = email;
        this.stockId = stockId;
        this.quantity = quantity;
        this.subtotal = subtotal;
        this.totalAmount = totalAmount;
        this.transactionId = transactionId;
    }
    
    public String getTransactionId() {
    	return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
    	this.transactionId = transactionId;
    }

    public String getEmail() { 
    	return email;
    }
    public void setEmail(String email) { 
    	this.email = email;
    }

    public Long getStockId() { 
    	return stockId;
    }
    public void setStockId(Long stockId) { 
    	this.stockId = stockId;
    }

    public int getQuantity() { 
    	return quantity; 
    }
    public void setQuantity(int quantity) { 
    	this.quantity = quantity; 
    }

    public BigDecimal getBalance() { 
    	return balance;
    }
    public void setBalance(BigDecimal balance) { 
    	this.balance = balance;
    }

    public BigDecimal getSubtotal() { 
    	return subtotal;
    }
    public void setSubtotal(BigDecimal subtotal) { 
    	this.subtotal = subtotal;
    }

    public BigDecimal getTotalAmount() { 
    	return totalAmount;
    }
    public void setTotalAmount(BigDecimal totalAmount) { 
    	this.totalAmount = totalAmount;
    }

}
