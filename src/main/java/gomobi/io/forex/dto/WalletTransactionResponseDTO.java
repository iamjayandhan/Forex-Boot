package gomobi.io.forex.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import gomobi.io.forex.enums.TransactionType;

public class WalletTransactionResponseDTO {
    private Long id;
    private Long userId;
    private TransactionType transactionType;
    private BigDecimal amount;
    private BigDecimal balance;
    private Timestamp createdAt;

    // Constructors
    public WalletTransactionResponseDTO() {}

    public WalletTransactionResponseDTO(Long id, Long userId, TransactionType transactionType, BigDecimal amount, BigDecimal balance, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balance = balance;
        this.createdAt = createdAt;
    }

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
