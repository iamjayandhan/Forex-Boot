package gomobi.io.forex.dto;

import java.math.BigDecimal;

public class BuyRequestDTO {
    private String email;             // To identify user
    private Long stockId;             // Stock to buy
    private int quantity;             // Number of units to buy
    private BigDecimal balance;       // User balance

    // Transaction-specific fields
    private String transactionType = "BUY";
    private BigDecimal pricePerUnit;
    private BigDecimal subtotal;

    // Detailed charges
    private BigDecimal brokerage;
    private BigDecimal exchangeTxnCharges;
    private BigDecimal stampDuty;
    private BigDecimal ipft;
    private BigDecimal sebiCharges;
    private BigDecimal stt;
    private BigDecimal gst;

    private BigDecimal totalAmount;

    // Holding-specific
    private BigDecimal avgPrice;

    // ✅ Constructor
    public BuyRequestDTO(String email, Long stockId, int quantity,
                         BigDecimal pricePerUnit, BigDecimal subtotal,
                         BigDecimal brokerage, BigDecimal exchangeTxnCharges,
                         BigDecimal stampDuty, BigDecimal ipft,
                         BigDecimal sebiCharges, BigDecimal stt, BigDecimal gst,
                         BigDecimal totalAmount, BigDecimal avgPrice) {
        this.email = email;
        this.stockId = stockId;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.subtotal = subtotal;
        this.brokerage = brokerage;
        this.exchangeTxnCharges = exchangeTxnCharges;
        this.stampDuty = stampDuty;
        this.ipft = ipft;
        this.sebiCharges = sebiCharges;
        this.stt = stt;
        this.gst = gst;
        this.totalAmount = totalAmount;
        this.avgPrice = avgPrice;
    }

    // ✅ Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Long getStockId() { return stockId; }
    public void setStockId(Long stockId) { this.stockId = stockId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public BigDecimal getPricePerUnit() { return pricePerUnit; }
    public void setPricePerUnit(BigDecimal pricePerUnit) { this.pricePerUnit = pricePerUnit; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getBrokerage() { return brokerage; }
    public void setBrokerage(BigDecimal brokerage) { this.brokerage = brokerage; }

    public BigDecimal getExchangeTxnCharges() { return exchangeTxnCharges; }
    public void setExchangeTxnCharges(BigDecimal exchangeTxnCharges) { this.exchangeTxnCharges = exchangeTxnCharges; }

    public BigDecimal getStampDuty() { return stampDuty; }
    public void setStampDuty(BigDecimal stampDuty) { this.stampDuty = stampDuty; }

    public BigDecimal getIpft() { return ipft; }
    public void setIpft(BigDecimal ipft) { this.ipft = ipft; }

    public BigDecimal getSebiCharges() { return sebiCharges; }
    public void setSebiCharges(BigDecimal sebiCharges) { this.sebiCharges = sebiCharges; }

    public BigDecimal getStt() { return stt; }
    public void setStt(BigDecimal stt) { this.stt = stt; }

    public BigDecimal getGst() { return gst; }
    public void setGst(BigDecimal gst) { this.gst = gst; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getAvgPrice() { return avgPrice; }
    public void setAvgPrice(BigDecimal avgPrice) { this.avgPrice = avgPrice; }
}
