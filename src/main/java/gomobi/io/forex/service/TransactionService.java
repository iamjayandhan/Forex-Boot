package gomobi.io.forex.service;

public interface TransactionService {
    String exportTransactionsToCSV(Long userId);

    String exportWalletTransactionsToCSV(Long userId);
}
