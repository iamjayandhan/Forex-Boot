package gomobi.io.forex.service;

public interface OtpService {
    boolean canSendOtp(String email);
    String generateOtp();
    void storeOtp(String email, String otpCode);
    boolean verifyOtp(String email, String enteredOtp);
    boolean isOtpVerified(String email);
    void clearOtp(String email);
    void cleanUpExpiredOtps();
}
