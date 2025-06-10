package gomobi.io.forex.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gomobi.io.forex.service.TransactionService;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    private final TransactionService transactionService;

    public ExportController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/transactions/{userId}")
    public ResponseEntity<byte[]> exportTransactionsCsv(@PathVariable Long userId) {
        String csvData = transactionService.exportTransactionsToCSV(userId);
        byte[] bytes = csvData.getBytes();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(bytes);
    }

    @GetMapping("/wallet-transactions/{userId}")
    public ResponseEntity<byte[]> exportWalletTransactionsCsv(@PathVariable Long userId) {
        String csvData = transactionService.exportWalletTransactionsToCSV(userId);
        byte[] bytes = csvData.getBytes();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=wallet-transactions.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(bytes);
    }
}
