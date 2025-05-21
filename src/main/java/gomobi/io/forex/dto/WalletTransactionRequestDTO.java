package gomobi.io.forex.dto;

import java.math.BigDecimal;

import gomobi.io.forex.enums.TransactionType;

public class WalletTransactionRequestDTO {
	private Long userId;
	private TransactionType transactionType;
	private BigDecimal amount;
	private BigDecimal balance;
	
	public WalletTransactionRequestDTO(){}
	
	public WalletTransactionRequestDTO(Long userId, TransactionType transactionType, BigDecimal amount,BigDecimal balance) {
		this.userId = userId;
		this.transactionType = transactionType;
		this.amount = amount;
		this.balance = balance;
	}
	
	//getters and setters
	public Long getUserId() {
		return userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public TransactionType getTransactionType() {
		return transactionType;
	}
	
	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}
	
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public BigDecimal getBalance() {
		return balance;
	}
	
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
}
