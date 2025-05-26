package gomobi.io.forex.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import gomobi.io.forex.dto.SuccessResponse;
import gomobi.io.forex.dto.UserProfileDto;
import gomobi.io.forex.dto.UserResponseDTO;
import gomobi.io.forex.entity.UserEntity;
import gomobi.io.forex.exception.DuplicateEmailException;
import gomobi.io.forex.exception.DuplicateUsernameException;
import gomobi.io.forex.exception.InvalidCredentialsException;
import gomobi.io.forex.exception.WeakPasswordException;
import gomobi.io.forex.repository.UserRepository;
import gomobi.io.forex.security.JwtUtil;
import gomobi.io.forex.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;
    
	@Autowired
	private AuthenticationManager authManager;
    
    // Constructor for UserRepository and BCryptPasswordEncoder dependency injection
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserEntity registerUser(UserEntity user) {
 
        if (userRepository.existsByEmail(user.getEmail())) {
        	 throw new DuplicateEmailException("Email already registered.");        
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new DuplicateUsernameException("Username already taken.");
        }
        
        if (!isValidPassword(user.getPassword())) {
            throw new WeakPasswordException("Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one digit, and one special character.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)));
        user.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)));
        user.setIsActive(true);
        user.setRole("USER");

        return userRepository.save(user);
    }

    @Override
    public SuccessResponse<UserResponseDTO> loginUser(String email, String password, HttpServletResponse response) {
        // Authenticate
    	System.out.println(email+" "+password);
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or email"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        Authentication authentication = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, password));
        
        // Now `authentication` is your official Spring Security authenticated object
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);

        // Prepare UserResponseDTO
        UserResponseDTO body = new UserResponseDTO(
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                token
        );

        // Prepare response structure
        SuccessResponse<UserResponseDTO> successResponse = new SuccessResponse<>(HttpStatus.OK.value(),"Login successful",body);

        // Set the token as a cookie
        Cookie cookie = new Cookie("auth_token", token);
        cookie.setHttpOnly(true);  // Ensure it's not accessible by JavaScript
        cookie.setSecure(true);     // Set to true in production (ensure the use of HTTPS)
        cookie.setPath("/");       // Make it available throughout the domain
        cookie.setMaxAge(60 * 60);   // Set the cookie expiration (3600 sec = 1hr)

        // Add the cookie to the response
        response.addCookie(cookie);

        return successResponse;
    }
    
    public boolean updateUserPassword(String email, String newPassword) {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }
    
    private static boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";
        return password != null && password.matches(passwordRegex);
    }

    @Override
    public Optional<?> updateUserDetails(String email, String fullName, String mobileNumber, LocalDate dateOfBirth) {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();

            if (fullName != null) user.setFullName(fullName);
            if (mobileNumber != null) user.setMobileNumber(mobileNumber);
            if (dateOfBirth != null) user.setDateOfBirth(dateOfBirth);

            userRepository.save(user);

            UserProfileDto updatedDto = new UserProfileDto(email, fullName, mobileNumber, dateOfBirth);
            return Optional.of(updatedDto);
        } else {
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<UserProfileDto> updateBalance(String email, BigDecimal amount, String operation) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();

            if ("deposit".equalsIgnoreCase(operation)) {
                user.setBalance(user.getBalance().add(amount));
            } 
            else if ("withdraw".equalsIgnoreCase(operation)) {
                if (user.getBalance().compareTo(amount) >= 0) {
                    user.setBalance(user.getBalance().subtract(amount));
                } else {
                    return Optional.empty(); // Not enough balance
                }
            } else {
                return Optional.empty(); // Invalid operation
            }

            userRepository.save(user);
            return Optional.of(new UserProfileDto(user));
        }

        return Optional.empty(); // User not found
    }
}
