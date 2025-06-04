package gomobi.io.forex.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "transactions")
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "stock_id", nullable = false)
    private StockEntity stock;

    @Column(nullable = false)
    private String transactionType; // BUY or SELL
    
    @Column(nullable = false)
    private String fpxTxnId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal pricePerUnit;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal brokerage = BigDecimal.ZERO; // New field

    @Column(name = "exchange_txn_charges", nullable = false, precision = 19, scale = 4)
    private BigDecimal exchangeTxnCharges = BigDecimal.ZERO; // New field

    @Column(name = "stamp_duty", nullable = false, precision = 19, scale = 4)
    private BigDecimal stampDuty = BigDecimal.ZERO; // New field

    @Column(name = "ipft", nullable = false, precision = 19, scale = 4)
    private BigDecimal ipft = BigDecimal.ZERO; // New field

    @Column(name = "sebi_charges", nullable = false, precision = 19, scale = 4)
    private BigDecimal sebiCharges = BigDecimal.ZERO; // New field

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal stt = BigDecimal.ZERO; // New field

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal gst = BigDecimal.ZERO; // New field

    @Column(name = "totalAmount", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalAmount; // subtotal + transaction fee + tax

    private Timestamp timestamp;

    @PrePersist
    public void onPrePersist() {
        this.timestamp = Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
    }

    // Getters and Setters
    
    public String getFpxTxnId() {
    	return fpxTxnId;
    }
    
    public void setFpxTxnId(String fpxTxnId) {
    	this.fpxTxnId = fpxTxnId;
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

    public StockEntity getStock() {
        return stock;
    }

    public void setStock(StockEntity stock) {
        this.stock = stock;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    // Subtotal
    public BigDecimal getSubTotal() {
        return subtotal;
    }

    public void setSubTotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    // Brokerage
    public BigDecimal getBrokerage() {
        return brokerage;
    }

    public void setBrokerage(BigDecimal brokerage) {
        this.brokerage = brokerage;
    }

    // Exchange Transaction Charges
    public BigDecimal getExchangeTxnCharges() {
        return exchangeTxnCharges;
    }

    public void setExchangeTxnCharges(BigDecimal exchangeTxnCharges) {
        this.exchangeTxnCharges = exchangeTxnCharges;
    }

    // Stamp Duty
    public BigDecimal getStampDuty() {
        return stampDuty;
    }

    public void setStampDuty(BigDecimal stampDuty) {
        this.stampDuty = stampDuty;
    }

    // IPFT
    public BigDecimal getIpft() {
        return ipft;
    }

    public void setIpft(BigDecimal ipft) {
        this.ipft = ipft;
    }

    // SEBI Charges
    public BigDecimal getSebiCharges() {
        return sebiCharges;
    }

    public void setSebiCharges(BigDecimal sebiCharges) {
        this.sebiCharges = sebiCharges;
    }

    // STT
    public BigDecimal getStt() {
        return stt;
    }

    public void setStt(BigDecimal stt) {
        this.stt = stt;
    }

    // GST
    public BigDecimal getGst() {
        return gst;
    }

    public void setGst(BigDecimal gst) {
        this.gst = gst;
    }

    // Total Amount
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
