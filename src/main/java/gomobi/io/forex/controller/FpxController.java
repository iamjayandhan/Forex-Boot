package gomobi.io.forex.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/fpx")
public class FpxController {
	
	@PostMapping("/payment-response")
	public void handlePaymentResponse(@RequestParam Map<String, String> params, HttpServletResponse response) throws IOException {
	    System.out.println("\n\n\n\n\n\n HIT \n\n\n\n\n\n");
		
		// Extract required values
	    String status = params.get("fpx_debitAuthCode").equals("00") ? "success" : "failed";
	    String orderNo = params.get("fpx_sellerOrderNo");
	    String txnId = params.get("fpx_fpxTxnId");

	    System.out.println(params);

	    // Redirect to Angular payment-status page with status
	    String redirectUrl = String.format("http://localhost:4200/payment-status?status=%s&orderNo=%s&transactionId=%s",
	            status, orderNo, txnId);

	    response.sendRedirect(redirectUrl);
	}
	 
    
//    @Autowired
//    private FpxService fpxService;

//    @PostMapping("/banks")
//    public ResponseEntity<?> getAvailableBanks() {
//        return ResponseEntity.ok(fpxService.getBankList());
//    }
    
//    @PostMapping("/prepare")
//    public Map<String, String> prepareFpxPayment(@RequestBody FpxRequestDto dto) {
//        return fpxService.prepareFpxForm(
//        		dto.getAmount(),
//        		dto.getSellerOrderNo(),
//        		dto.getBankType(),
//        		dto.getCustomerName(),
//        		dto.getMerchantName(),
//        		dto.getBankCode(),
//        		dto.getCustomerEmail()
//        );
//    }
}
