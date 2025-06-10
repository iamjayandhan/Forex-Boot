package gomobi.io.forex.dto;

import java.math.BigDecimal;

public class PendingOrderDto {
    private Long userId;
    private Long stockId;
    private int quantity;
    private BigDecimal subTotalPrice;
    private BigDecimal extraCharges;

    // Getter and Setter for userId
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // Getter and Setter for stockId
    public Long getStockId() {
        return stockId;
    }
    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    // Getter and Setter for quantity
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Getter and Setter for subTotalPrice
    public BigDecimal getSubTotalPrice() {
        return subTotalPrice;
    }
    public void setSubTotalPrice(BigDecimal subTotalPrice) {
        this.subTotalPrice = subTotalPrice;
    }

    // Getter and Setter for extraCharges
    public BigDecimal getExtraCharges() {
        return extraCharges;
    }
    public void setExtraCharges(BigDecimal extraCharges) {
        this.extraCharges = extraCharges;
    }
}
