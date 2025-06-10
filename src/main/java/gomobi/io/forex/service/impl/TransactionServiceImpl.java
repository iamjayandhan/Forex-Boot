package gomobi.io.forex.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gomobi.io.forex.entity.TransactionEntity;
import gomobi.io.forex.entity.WalletTransactionEntity;
import gomobi.io.forex.repository.TransactionRepository;
import gomobi.io.forex.repository.WalletTransactionRepository;
import gomobi.io.forex.service.TransactionService;

@Service
public class TransactionServiceImpl implements TransactionService {

	@Autowired
    private TransactionRepository transactionRepository;
	@Autowired
    private WalletTransactionRepository walletTransactionRepository;

	@Override
    public String exportTransactionsToCSV(Long userId) {
        List<TransactionEntity> transactions = transactionRepository.findByUserId(userId);

        String header = "ID,UserID,StockID,Type,FPXTxnID,Quantity,PricePerUnit,Subtotal,Brokerage,ExchangeTxnCharges,StampDuty,IPFT,SEBICharges,STT,GST,TotalAmount,Status,Timestamp\n";

        String rows = transactions.stream()
            .map(tx -> String.format("%d,%d,%d,%s,%s,%d,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%s,%s",
                    tx.getId(),
                    tx.getUser().getId(),
                    tx.getStock().getId(),
                    tx.getTransactionType(),
                    tx.getFpxTxnId(),
                    tx.getQuantity(),
                    tx.getPricePerUnit(),
                    tx.getSubTotal(),
                    tx.getBrokerage(),
                    tx.getExchangeTxnCharges(),
                    tx.getStampDuty(),
                    tx.getIpft(),
                    tx.getSebiCharges(),
                    tx.getStt(),
                    tx.getGst(),
                    tx.getTotalAmount(),
                    tx.getStatus(),
                    tx.getTimestamp()))
            .collect(Collectors.joining("\n"));

        return header + rows;
    }

    @Override
    public String exportWalletTransactionsToCSV(Long userId) {
        List<WalletTransactionEntity> walletTxs = walletTransactionRepository.findByUserId(userId);

        String header = "ID,UserID,Type,Reason,Amount,Timestamp\n";

        String rows = walletTxs.stream()
            .map(tx -> String.format("%d,%d,%s,%s,%.2f,%s",
                    tx.getId(),
                    tx.getUser().getId(),
                    tx.getTransactionType(),
                    tx.getTransactionReason(),
                    tx.getAmount(),
                    tx.getTimestamp()))
            .collect(Collectors.joining("\n"));

        return header + rows;
    }
}

