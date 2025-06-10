package gomobi.io.forex.service.impl;

import gomobi.io.forex.service.FpxService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gomobi.io.forex.entity.*;
import gomobi.io.forex.enums.StatusEnum;
import gomobi.io.forex.repository.*;

@Service
public class FpxServiceImpl implements FpxService {

    @Autowired private UserRepository userRepository;
    @Autowired private StockRepository stockRepository;
    @Autowired private PendingOrderRepository pendingOrderRepository;
    @Autowired private HoldingRepository holdingRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private WalletTransactionRepository walletTransactionRepository;
    
    @Override
    @Transactional
    public void processOrderPaymentResponse(Long userId, Long stockId, Integer quantity, String txnId, boolean success) {
        if (success) {
            processSuccessfulOrder(userId, stockId, quantity, txnId);
        } else {
            System.out.println("Payment failed for userId: " + userId + ", stockId: " + stockId);
            pendingOrderRepository.deleteByUserIdAndStockId(userId, stockId);
        }
    }

    @Override
    @Transactional
    public void processSuccessfulOrder(Long userId, Long stockId, Integer quantity, String txnId) {
        Optional<PendingOrderEntity> pendingOpt = pendingOrderRepository
                .findFirstByUserIdAndStockIdAndQuantityOrderByCreatedAtDesc(userId, stockId, quantity);

        if (pendingOpt.isEmpty()) {
            throw new RuntimeException("Pending order not found for user " + userId + ", stock " + stockId + ", quantity " + quantity);
        }

        PendingOrderEntity pendingOrder = pendingOpt.get();

        BigDecimal subTotalPrice = pendingOrder.getsubTotalPrice();
        BigDecimal extraCharges = pendingOrder.getExtraCharges();

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StockEntity stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        BigDecimal pricePerUnit = stock.getCurrentPrice();
        BigDecimal qtyBD = BigDecimal.valueOf(quantity);

        BigDecimal totalPrice = subTotalPrice.add(extraCharges);
        user.setBalance(user.getBalance().subtract(totalPrice));
        userRepository.save(user);

        stock.setIpoQty(stock.getIpoQty() - quantity);
        stockRepository.save(stock);

        Optional<HoldingEntity> holdingOpt = holdingRepository.findByUserAndStock(user, stock);
        if (holdingOpt.isPresent()) {
            HoldingEntity holding = holdingOpt.get();
            int oldQty = holding.getQuantity();
            BigDecimal oldAvg = holding.getAvgPrice();

            BigDecimal newAvg = oldAvg.multiply(BigDecimal.valueOf(oldQty))
                    .add(pricePerUnit.multiply(qtyBD))
                    .divide(BigDecimal.valueOf(oldQty + quantity), 2, RoundingMode.HALF_EVEN);

            holding.setQuantity(oldQty + quantity);
            holding.setAvgPrice(newAvg);
            holdingRepository.save(holding);
        } else {
            HoldingEntity holding = new HoldingEntity();
            holding.setUser(user);
            holding.setStock(stock);
            holding.setQuantity(quantity);
            holding.setAvgPrice(pricePerUnit);
            holdingRepository.save(holding);
        }

        transactionRepository
                .findFirstByUserIdAndStockIdAndStatusOrderByTimestampDesc(userId, stockId, StatusEnum.FAILURE)
                .ifPresent(txn -> {
                    txn.setStatus(StatusEnum.SUCCESS);
                    txn.setFpxTxnId(txnId);
                    transactionRepository.save(txn);
                });

        walletTransactionRepository
                .findFirstByUserIdAndStatusOrderByTimestampDesc(userId, StatusEnum.FAILURE)
                .ifPresent(wtxn -> {
                    wtxn.setBalance(user.getBalance());
                    wtxn.setStatus(StatusEnum.SUCCESS);
                    walletTransactionRepository.save(wtxn);
                });

        pendingOrderRepository.delete(pendingOrder);
    }
}
