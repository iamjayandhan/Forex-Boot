package gomobi.io.forex.service;

import gomobi.io.forex.enums.OtpPurpose;

public interface OtpService {
    boolean canSendOtp(String email);
    String generateOtp();
    void storeOtp(String email, String otpCode, OtpPurpose purpose);
    boolean verifyOtp(String email, String enteredOtp, OtpPurpose purpose);
    boolean isOtpVerified(String email);
    void clearOtp(String email);
    void cleanUpExpiredOtps();
}
