package gomobi.io.forex.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonInclude;

import gomobi.io.forex.enums.TransactionReason;
import gomobi.io.forex.enums.TransactionType;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WalletTransactionDTO {
    private Long id;
    private Long userId;
    private TransactionType transactionType;
    private TransactionReason transactionReason;
    private BigDecimal amount;
    private BigDecimal balance;
    private Timestamp createdAt;

    // Constructors

    public WalletTransactionDTO(Long id, Long userId, TransactionType transactionType, TransactionReason transactionReason,BigDecimal amount, BigDecimal balance, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.transactionType = transactionType;
        this.transactionReason = transactionReason;
        this.amount = amount;
        this.balance = balance;
        this.createdAt = createdAt;
    }
    
    public WalletTransactionDTO(Long id, TransactionType transactionType, TransactionReason transactionReason,BigDecimal amount, BigDecimal balance, Timestamp createdAt) {
    	this.id = id;
    	this.transactionType = transactionType;
    	this.transactionReason = transactionReason;
    	this.amount = amount;
    	this.balance = balance;
    	this.createdAt = createdAt;
    }
    
    public WalletTransactionDTO() {}

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
    
    public TransactionReason getTransactionReason() {
    	return transactionReason;
    }
    
    public void setTransactionReason(TransactionReason transactionReason) {
    	this.transactionReason = transactionReason;
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
