package gomobi.io.forex.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gomobi.io.forex.dto.BalanceUpdateRequestDTO;
import gomobi.io.forex.dto.OTPRequestDTO;
import gomobi.io.forex.dto.SuccessResponse;
import gomobi.io.forex.dto.UserDTO;
import gomobi.io.forex.dto.UserProfileDto;
import gomobi.io.forex.dto.UserResponseDTO;
import gomobi.io.forex.entity.UserEntity;
import gomobi.io.forex.enums.OtpPurpose;
import gomobi.io.forex.exception.ErrorResponse;
import gomobi.io.forex.repository.UserRepository;
import gomobi.io.forex.service.UserService;
import gomobi.io.forex.service.impl.OtpServiceImpl;
import gomobi.io.forex.util.MailUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired	
    private MailUtil mailUtil;
    
    @Autowired
    private OtpServiceImpl otpService;
    
    // Constructor for UserService dependency injection
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getMe() {
    	
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	 
        if (authentication == null || !authentication.isAuthenticated()) {
            ErrorResponse responseBody = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
        }

        String username = authentication.getName();
        System.out.println(username);
        UserEntity user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserProfileDto profile = new UserProfileDto(user);

        Map<String, Object> responseBody = Map.of(
            "status", HttpStatus.ACCEPTED.value(),
            "message", "Authorized.",
            "data", profile
        );

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseBody);
    }


    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDTO userDTO, BindingResult result) {
        if (result.hasErrors()) {
            String errorMessage = result.getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));
            ErrorResponse responseBody = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),errorMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        }

        UserEntity userEntity = convertToEntity(userDTO); 
        userService.registerUser(userEntity); // Now returns the saved entity

        SuccessResponse<Object> responseBody = new SuccessResponse<>(HttpStatus.CREATED.value(), "User registered successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    
    @CrossOrigin(origins="http://localhost:4200")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData,HttpServletResponse response) {
        String email = loginData.get("email");
        String password = loginData.get("password");
        
        SuccessResponse<UserResponseDTO> responseBody = userService.loginUser(email, password, response);     
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<SuccessResponse<Object>> logout(HttpServletResponse response) {
        // Create a cookie with the same name and set max age to 0 to delete it
        Cookie cookie = new Cookie("auth_token", null);
        cookie.setHttpOnly(true);
        //cookie.setSecure(true); // Set true if using HTTPS
        cookie.setPath("/");    // Must match the path used when the cookie was set
        cookie.setMaxAge(0);    // This deletes the cookie

        response.addCookie(cookie);
        
        SuccessResponse<Object> responseBody = new SuccessResponse<Object>(HttpStatus.OK.value(),"Logged out successfully");

        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    // Endpoint to send OTP
    @PostMapping("/sendOtp")
    public ResponseEntity<?> sendOtp(@RequestBody OTPRequestDTO request) {
        String email = request.getEmail();

        if (email == null || email.isEmpty() || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Invalid email format."));
        }

        if (!userRepository.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Email does not exist."));
        }

        if (!otpService.canSendOtp(email)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new ErrorResponse(HttpStatus.TOO_MANY_REQUESTS.value(), "Please wait for a while before generating new OTP."));
        }

        String otp = otpService.generateOtp();

        otpService.storeOtp(email, otp,OtpPurpose.FORGOT_PASSWORD);

        System.out.println("Generate Email: " + email);
        System.out.println("Generate OTP: " + otp);

        boolean sent = mailUtil.sendOtpEmail(email, otp);
        if (!sent) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to send OTP. Please try again."));
        }

        return ResponseEntity.ok(new SuccessResponse<>(HttpStatus.OK.value(), "OTP has been sent to email."));
    }

    @PostMapping("/verifyOtp")
    public ResponseEntity<?> verifyOtp(@RequestBody OTPRequestDTO request) {
        String email = request.getEmail();
        String inputOtp = request.getOtp();

        if (email == null || email.isEmpty() || inputOtp == null || inputOtp.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "email or otp is empty."));
        }

        if (!userRepository.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Email does not exist."));
        }
        
        boolean result = otpService.verifyOtp(email, inputOtp,OtpPurpose.FORGOT_PASSWORD);

        if (!result) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired OTP. Please try again."));
        }

        return ResponseEntity.ok(new SuccessResponse<>(HttpStatus.OK.value(), "OTP verified successfully."));
    }

    @PostMapping("/resetPwd")
    public ResponseEntity<?> resetPassword(@RequestBody OTPRequestDTO request) {
        String email = request.getEmail();
        String newPassword = request.getNewPassword();

        // Validate input
        if (email == null || email.isEmpty() || newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Email or new password is empty."));
        }

        // Check if the email exists in the user table (optional)
        if (!userRepository.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Email not found."));
        }

        // Check if OTP is verified for the email
        boolean isOtpVerified = otpService.isOtpVerified(email);

        if (!isOtpVerified) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "OTP is invalid, expired, or not verified."));
        }

        // Proceed with password reset if OTP is verified
        String passwordUpdated = userService.updateUserPassword(email, newPassword);

        if (passwordUpdated == "Success") {
            otpService.clearOtp(email);  // Clear OTP after successful password reset
            return ResponseEntity.ok(new SuccessResponse<>(HttpStatus.OK.value(), "Password reset successful."));
        } else if(passwordUpdated == "Duplicate") {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "New password cannot be the old password."));
        } else {
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to reset password."));
        }
    }
    
    //for email verification
    @PostMapping("/sendEmailVerificationOtp")
    public ResponseEntity<?> sendEmailVerificationOtp(@RequestBody OTPRequestDTO request) {
        String email = request.getEmail();

        if (email == null || email.isEmpty() || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Invalid email format."));
        }
        
        //check if the email is already used?
        if(userRepository.existsByEmail(email)) {
        	ErrorResponse responseBody = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),"Email already in use.");
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        }
        

        if (!otpService.canSendOtp(email)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new ErrorResponse(HttpStatus.TOO_MANY_REQUESTS.value(), "Please wait before generating a new OTP."));
        }

        String otp = otpService.generateOtp();
        otpService.storeOtp(email, otp, OtpPurpose.VERIFY_EMAIL);

        System.out.println("Generate Email: " + email);
        System.out.println("Generate OTP: " + otp);

        boolean sent = mailUtil.sendOtpEmail(email, otp);
        if (!sent) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to send OTP. Please try again."));
        }

        return ResponseEntity.ok(new SuccessResponse<>(HttpStatus.OK.value(), "OTP has been sent for email verification."));
    }
    
    @PostMapping("/verifyEmailVerificationOtp")
    public ResponseEntity<?> verifyEmailVerificationOtp(@RequestBody OTPRequestDTO request) {
        String email = request.getEmail();
        String inputOtp = request.getOtp();

        if (email == null || email.isEmpty() || inputOtp == null || inputOtp.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Email or OTP is empty."));
        }

        boolean result = otpService.verifyOtp(email, inputOtp, OtpPurpose.VERIFY_EMAIL);

        if (!result) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired OTP."));
        }

        return ResponseEntity.ok(new SuccessResponse<>(HttpStatus.OK.value(), "Email verified successfully."));
    }

    
    @CrossOrigin(origins="http://localhost:5500")
    @GetMapping("/fetchAll")
    public ResponseEntity<SuccessResponse<List<UserEntity>>> fetchAll(){
    	List<UserEntity> allUsers = userRepository.findAll();
    	return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse<>(HttpStatus.OK.value(),"All data fetched successfully",allUsers));
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UserProfileDto request){
        Optional<?> updatedUser = userService.updateUserDetails(
                request.getEmail(),
                request.getFullName(),
                request.getMobileNumber(),
                request.getDateOfBirth()
        );

        if (updatedUser.isPresent()) {
            SuccessResponse<Object> responseBody =
                    new SuccessResponse<>(HttpStatus.OK.value(), "User updated successfully!", updatedUser.get());
            return ResponseEntity.ok(responseBody);
        } else {
            ErrorResponse responseBody =
                    new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Failed to update user. Please check the provided data.");
            return ResponseEntity.badRequest().body(responseBody);
        }
    }
    
    @PostMapping("/balance")
    public ResponseEntity<?> updateUserBalance(@RequestBody BalanceUpdateRequestDTO request) {
        Optional<UserProfileDto> result = userService.updateBalance(
                request.getEmail(),
                request.getAmount(),
                request.getOperation()
        );

        if (result.isPresent()) {
            SuccessResponse<Object> responseBody =
                    new SuccessResponse<>(HttpStatus.OK.value(), "Balance updated successfully!", result.get());
            return ResponseEntity.ok(responseBody);
        } else {
            ErrorResponse responseBody =
                    new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Failed to update balance. Please check the operation or available balance.");
            return ResponseEntity.badRequest().body(responseBody);
        }
    }
    
    public UserEntity convertToEntity(UserDTO dto) {
        UserEntity entity = new UserEntity();
        entity.setUsername(dto.getUsername());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
        entity.setFullName(dto.getFullName());
        entity.setMobileNumber(dto.getMobileNumber());
        entity.setDateOfBirth(dto.getDateOfBirth());
        entity.setMpin(dto.getMpin());
        entity.setIsActive(true);
        return entity;
    }  
}
