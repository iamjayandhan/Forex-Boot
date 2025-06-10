package gomobi.io.forex.dto;

import java.math.BigDecimal;

public class EncryptRequest {
    private BigDecimal amount;
    private String sellerOrderNo;
    private String subMid;
    private String param1;
    private String param2;

    // Getters and Setters
    public BigDecimal getAmount() {
    	return amount;
    }
    
    public void setAmount(BigDecimal amount) {
    	this.amount = amount;
    }
    
    public String getSellerOrderNo() {
    	return sellerOrderNo;
    }
    
    public void setSellerOrderNo(String sellerOrderNo) {
    	this.sellerOrderNo = sellerOrderNo;
    }
    
    public String getSubMid() {
    	return subMid;
    }
    
    public void setSubMid(String subMid) {
    	this.subMid = subMid;
    }

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }
}