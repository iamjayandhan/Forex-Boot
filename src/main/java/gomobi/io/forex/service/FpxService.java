//package gomobi.io.forex.service;
//
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.Map;
//
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
////for the checksum
//import javax.crypto.Mac;
//import javax.crypto.spec.SecretKeySpec;
//import java.util.Base64;
//
//@Service
//public class FpxService {
//
////    private final String FPX_URL = "https://services.gomobi.io/api/fpx";
//    
//    //i used same secret key that i used for JWT!
//    private static final String SECRET_KEY = "k6DpnRwn8NLBaBEGuAJAaaix15mGq9VH";
//
////    public String getBankList() {
////	    try {
////	        HttpHeaders headers = new HttpHeaders();
////	        headers.setContentType(MediaType.APPLICATION_JSON);
////	
////	        Map<String, String> payload = new HashMap<>();
////	        payload.put("Service", "FULL_LIST");
////	
////	        HttpEntity<Map<String, String>> entity = new HttpEntity<>(payload, headers);
////	
////	        RestTemplate restTemplate = new RestTemplate();
////	        ResponseEntity<String> response = restTemplate.postForEntity(FPX_URL, entity, String.class);
////	
////	        return response.getBody();
////	    } catch (Exception ex) {
////	        ex.printStackTrace();
////	        return "Error occurred: " + ex.getMessage();
////	    }
////	}
//    
//    public Map<String, String> prepareFpxForm(
//            String amount, String sellerOrderNo, String bankType,
//            String customerName, String merchantName, String bankCode, String customerEmail
//    ) {
//        String redirectUrl = "http://localhost:4200/payment/redirect";
//
//        // Use only amount and sellerOrderNo in checksum logic
//        String minified = amount + "|" + sellerOrderNo;
//        String checkSum = generateCheckSum(minified);
//
//        Map<String, String> form = new LinkedHashMap<>();
//        form.put("amount", amount);
//        form.put("redirectUrl", redirectUrl);
//        form.put("sellerOrderNo", sellerOrderNo);
//        form.put("bankType", bankType);
//        form.put("mid", "FPX000000054555");
//        form.put("buyerName", customerName);
//        form.put("tid", "27965678");
//        form.put("merchantName", merchantName);
//        form.put("bank", bankCode);
//        form.put("service","FULL_LIST");
//        form.put("email", customerEmail);
//        form.put("subMID","201100000012450");
//        form.put("checkSum", checkSum);
//
//        return form;
//    }
//
//    public String generateCheckSum(String data) {
//        try {
//            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
//            SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");
//            sha256_HMAC.init(secretKeySpec);
//
//            byte[] hash = sha256_HMAC.doFinal(data.getBytes());
//            return Base64.getEncoder().encodeToString(hash);
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to generate checksum", e);
//        }
//    }
//
//    
//
//
//}
