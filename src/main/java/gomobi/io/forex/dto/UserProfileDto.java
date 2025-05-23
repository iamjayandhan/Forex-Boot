package gomobi.io.forex.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import gomobi.io.forex.entity.UserEntity;

public class UserProfileDto {
	private Long id;
    private String username;
    private String fullName;
    private String email;
    private String mobileNumber;
    private LocalDate dateOfBirth;
    private boolean isActive;
    private String role;
    private String mpin;
    private BigDecimal balance;

    public UserProfileDto(UserEntity user) {
    	this.id = user.getId();
    	this.username = user.getUsername();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.mobileNumber = user.getMobileNumber();
        this.dateOfBirth = user.getDateOfBirth();
        this.isActive = user.isActive();
        this.role = user.getRole();
        this.mpin = user.getMpin();
        this.balance = user.getBalance();
    }
    
    public UserProfileDto(String fullName, String email,String mobileNumber, LocalDate dateOfBirth) {
    	this.fullName = fullName;
    	this.email = email;
    	this.mobileNumber = mobileNumber;
    	this.dateOfBirth = dateOfBirth;
    }
    
    public UserProfileDto() {}
    
    // Getters and Setters
    public Long getUserId() {
    	return id;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
    public String getMpin() {
        return mpin;
    }

    public void setMpin(String mpin) {
        this.mpin = mpin;
    }
    
    public BigDecimal getBalance() {
    	return this.balance;
    }
    
    public void setBalance(BigDecimal balance) {
    	this.balance = balance;
    }
}
