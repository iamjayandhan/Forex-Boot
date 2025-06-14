package gomobi.io.forex.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import gomobi.io.forex.enums.StatusEnum;
import gomobi.io.forex.enums.TransactionReason;
import gomobi.io.forex.enums.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "walletTransactions")
public class WalletTransactionEntity {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType; // DEPOSIT or WITHDRAW
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionReason transactionReason; // STOCK_PURCHASE / STOCK_SELL / BONUS / REFUND / MAINTENANCE 
    
    @Column(nullable = false,precision = 19, scale = 4)
    private BigDecimal amount;
    
    @Column(nullable = false, precision = 19,scale = 4)
    private BigDecimal balance;
    
    private Timestamp timestamp;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('FAILURE','SUCCESS') DEFAULT 'FAILURE'")
    private StatusEnum status;

    
    @PrePersist
    public void onPrePersist() {
        this.timestamp = Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
    }
    
    //getters and setters
    
    public StatusEnum getStatus() {
    	return status;
    }
    
    public void setStatus(StatusEnum status) {
    	this.status = status;
    }
    
    public Long getId() {
    	return id;    
    }
    
    public void setId(Long id) {
    	this.id = id;
    }
    
    public UserEntity getUser() {
    	return user;
    }
    
    public void setUser(UserEntity user) {
    	this.user = user;
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
    
    public Timestamp getTimestamp() {
    	return timestamp;
    }
}
