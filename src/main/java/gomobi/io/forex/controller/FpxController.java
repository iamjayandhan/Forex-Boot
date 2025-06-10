package gomobi.io.forex.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gomobi.io.forex.dto.EncryptRequest;
import gomobi.io.forex.dto.PendingOrderDto;
import gomobi.io.forex.entity.HoldingEntity;
import gomobi.io.forex.entity.PendingOrderEntity;
import gomobi.io.forex.entity.StockEntity;
import gomobi.io.forex.entity.UserEntity;
import gomobi.io.forex.enums.StatusEnum;
import gomobi.io.forex.enums.StockTransactionType;
import gomobi.io.forex.repository.HoldingRepository;
import gomobi.io.forex.repository.PendingOrderRepository;
import gomobi.io.forex.repository.StockRepository;
import gomobi.io.forex.repository.TransactionRepository;
import gomobi.io.forex.repository.UserRepository;
import gomobi.io.forex.repository.WalletTransactionRepository;
import gomobi.io.forex.service.EncryptionService;
import gomobi.io.forex.service.FpxService;
import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/fpx")
public class FpxController {

	
	@Autowired
	private PendingOrderRepository pendingOrderRepository;
	
    @Autowired
    private EncryptionService encryptionService;
    
    @Autowired 
    private FpxService fpxService;

	@PostMapping("/create")
    public ResponseEntity<?> createPendingOrder(@RequestBody PendingOrderDto dto) {
        // Map DTO to entity
        PendingOrderEntity pendingOrder = new PendingOrderEntity();
        pendingOrder.setUserId(dto.getUserId());
        pendingOrder.setStockId(dto.getStockId());
        pendingOrder.setQuantity(dto.getQuantity());
        pendingOrder.setSubTotalPrice(dto.getSubTotalPrice());
        pendingOrder.setTransactionType(StockTransactionType.BUY);
        pendingOrder.setExtraCharges(dto.getExtraCharges());

        pendingOrderRepository.save(pendingOrder);

        Map<String, String> response = Map.of("message", "Pending order saved");
        return ResponseEntity.ok(response);
    }

	@PostMapping("/getCheckSum")
    public String encrypt(@RequestBody EncryptRequest request) {
		String minifiedString = request.getAmount() + "|" + request.getSellerOrderNo() + "|" + request.getSubMid();
        return encryptionService.encryptPayload(
        		minifiedString,
                request.getParam1(),
                request.getParam2()
        );
    }
	
	
  @PostMapping("/payment-response")
  public void handlePaymentResponse(@RequestParam Map<String, String> params, HttpServletResponse response) throws IOException {
      System.out.println("HIT");

      String status = "failed";
      if(params.containsKey("fpx_debitAuthCode") && "00".equals(params.get("fpx_debitAuthCode"))) {
          status = "success";
      }
      String orderNo = params.get("fpx_sellerOrderNo");
      String txnId = params.get("fpx_fpxTxnId");
      String authCode = params.get("fpx_debitAuthCode");

      // System.out.println("Params: " + params);
      System.out.println("\n\n\nAUTH CODE: "+ authCode);

      // Example orderNo = ORDX5X10X100X2450D75X50D00X123
      // We only parse userId, stockId, quantity
      String[] parts = orderNo.replaceFirst("^ORDX", "").split("X");

      if(parts.length >= 3) {
          try {
              Long userId = Long.parseLong(parts[0]);
              Long stockId = Long.parseLong(parts[1]);
              Integer quantity = Integer.parseInt(parts[2]);

              System.out.println("Parsed order details:");
              System.out.println("UserId: " + userId);
              System.out.println("StockId: " + stockId);
              System.out.println("Quantity: " + quantity);

              if (status.equals("success")) {
            	    fpxService.processOrderPaymentResponse(userId, stockId, quantity, txnId, true);
            	} else {
            	    fpxService.processOrderPaymentResponse(userId, stockId, quantity, txnId, false);
            	}


          } catch(NumberFormatException e) {
              System.err.println("Error parsing orderNo: " + e.getMessage());
          }
      } else {
          System.err.println("Invalid orderNo format: " + orderNo);
      }

      String redirectUrl = String.format("http://localhost:4200/payment-status?status=%s&orderNo=%s&transactionId=%s&authCode=%s",
              status, orderNo, txnId,authCode);

      response.sendRedirect(redirectUrl);
  }
	    
    //user balance - user table
    //stock qty market table
    //holdings table entry
    //transactions table - success
    //wallet transactions table - success
}
