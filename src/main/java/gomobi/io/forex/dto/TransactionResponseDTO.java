package gomobi.io.forex.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class TransactionResponseDTO {
    private Long id;
    private String transactionType;
    private int quantity;
    private BigDecimal pricePerUnit;
    private BigDecimal subTotal;

    private BigDecimal brokerage;
    private BigDecimal exchangeTxnCharges;
    private BigDecimal stampDuty;
    private BigDecimal ipft;
    private BigDecimal sebiCharges;
    private BigDecimal stt;
    private BigDecimal gst;

    private BigDecimal totalAmount;
    private Timestamp timestamp;

    private StockInfoDTO stock;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public BigDecimal getBrokerage() {
        return brokerage;
    }

    public void setBrokerage(BigDecimal brokerage) {
        this.brokerage = brokerage;
    }

    public BigDecimal getExchangeTxnCharges() {
        return exchangeTxnCharges;
    }

    public void setExchangeTxnCharges(BigDecimal exchangeTxnCharges) {
        this.exchangeTxnCharges = exchangeTxnCharges;
    }

    public BigDecimal getStampDuty() {
        return stampDuty;
    }

    public void setStampDuty(BigDecimal stampDuty) {
        this.stampDuty = stampDuty;
    }

    public BigDecimal getIpft() {
        return ipft;
    }

    public void setIpft(BigDecimal ipft) {
        this.ipft = ipft;
    }

    public BigDecimal getSebiCharges() {
        return sebiCharges;
    }

    public void setSebiCharges(BigDecimal sebiCharges) {
        this.sebiCharges = sebiCharges;
    }

    public BigDecimal getStt() {
        return stt;
    }

    public void setStt(BigDecimal stt) {
        this.stt = stt;
    }

    public BigDecimal getGst() {
        return gst;
    }

    public void setGst(BigDecimal gst) {
        this.gst = gst;
    }

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

    public StockInfoDTO getStock() {
        return stock;
    }

    public void setStock(StockInfoDTO stock) {
        this.stock = stock;
    }
}
