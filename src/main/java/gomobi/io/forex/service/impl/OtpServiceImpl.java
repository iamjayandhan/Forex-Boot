package gomobi.io.forex.service.impl;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import gomobi.io.forex.entity.OtpEntity;
import gomobi.io.forex.enums.OtpPurpose;
import gomobi.io.forex.repository.OtpRepository;
import gomobi.io.forex.service.OtpService;
import jakarta.transaction.Transactional;

@Service
public class OtpServiceImpl implements OtpService{

    @Autowired
    private OtpRepository otpRepository;

    private static final long OTP_EXPIRATION_TIME = 10 * 60 * 1000; // 10 mins
    private static final long OTP_COOLDOWN_TIME = 1 * 60 * 1000; // 1 mins

    /**
     * Check if user can request OTP (must wait 1 minute after last request).
     */
    public boolean canSendOtp(String email) {
        Optional<OtpEntity> existingOtp = otpRepository.findTopByEmailOrderByTimestampDesc(email);
        if (existingOtp.isEmpty()) return true;

        long lastGenerated = existingOtp.get().getTimestamp().getTime();
        long now = System.currentTimeMillis();
        return (now - lastGenerated) >= OTP_COOLDOWN_TIME;
    }

    /**
     * Generates a 6-digit OTP.
     */
    public String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    /**
     * Stores a new OTP and overwrites any existing one for the email.
     */
    @Transactional
    public void storeOtp(String email, String otpCode,OtpPurpose purpose) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp expiresAt = new Timestamp(now.getTime() + OTP_EXPIRATION_TIME);

        // Remove any existing OTP record before creating a new one
//        otpRepository.deleteByEmail(email);
        otpRepository.deleteByEmailAndPurpose(email,purpose);

        OtpEntity otp = new OtpEntity();
        otp.setEmail(email);
        otp.setOtp(otpCode);
        otp.setTimestamp(now);
        otp.setExpiresAt(expiresAt);
        otp.setVerified(false);
        otp.setPurpose(purpose);

        otpRepository.save(otp);
    }

    /**
     * Validates and marks OTP as verified if matched.
     */
    @Transactional
    public boolean verifyOtp(String email, String enteredOtp,OtpPurpose purpose) {
//        Optional<OtpEntity> record = otpRepository.findTopByEmailOrderByTimestampDesc(email);
        Optional<OtpEntity> record = otpRepository.findTopByEmailAndPurposeOrderByTimestampDesc(email,purpose);
        
        if (record.isEmpty()) return false;

        OtpEntity otp = record.get();
        long now = System.currentTimeMillis();

        if (now > otp.getExpiresAt().getTime()) {
            otpRepository.delete(otp); // Delete expired OTP from the database
            return false;
        }

        if (otp.getVerified()) { // OTP already verified
            return false;
        }

        if (otp.getOtp().equals(enteredOtp)) {
            otp.setVerified(true); // Mark OTP as verified
            otpRepository.save(otp);
            return true;
        }

        return false;
    }

    /**
     * Checks if OTP is verified for email.
     */
    public boolean isOtpVerified(String email) {
        Optional<OtpEntity> record = otpRepository.findTopByEmailOrderByTimestampDesc(email);
        return record.isPresent() && record.get().getVerified();
    }

    /**
     * Removes OTP for the given email (called after password reset).
     */
    @Transactional
    public void clearOtp(String email) {
        otpRepository.deleteByEmail(email);
    }
    
    @Transactional
    @Scheduled(fixedRate = 1000 * 60) // Runs every 1 minute
    public void cleanUpExpiredOtps() {
    	System.out.println("OTP clean scheduler called!");
        long now = System.currentTimeMillis();
        otpRepository.deleteByExpiresAtBefore(new Timestamp(now)); // Delete OTPs that have expired
    }
}
