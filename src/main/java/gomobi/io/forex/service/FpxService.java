package gomobi.io.forex.service;

public interface FpxService {
    void processSuccessfulOrder(Long userId, Long stockId, Integer quantity, String txnId);
    void processOrderPaymentResponse(Long userId, Long stockId, Integer quantity, String txnId, boolean success);
}
