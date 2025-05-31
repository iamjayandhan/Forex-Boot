package gomobi.io.forex.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import gomobi.io.forex.enums.OtpPurpose;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OTPRequestDTO {
    private String email;
    private String otp;
    private String newPassword;
    private OtpPurpose purpose;

    // Constructors
    public OTPRequestDTO() {}

    public OTPRequestDTO(String email, String otp, String newPassword,OtpPurpose purpose) {
        this.email = email;
        this.otp = otp;
        this.newPassword = newPassword;
        this.purpose = purpose;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    
    public OtpPurpose getOtpPurpose() {
    	return purpose;
    }
    
    public void setOtpPurpose(OtpPurpose purpose) {
    	this.purpose = purpose;
    }
}
