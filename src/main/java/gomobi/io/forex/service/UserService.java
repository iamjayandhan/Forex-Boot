package gomobi.io.forex.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import gomobi.io.forex.dto.SuccessResponse;
import gomobi.io.forex.dto.UserProfileDto;
import gomobi.io.forex.dto.UserResponseDTO;
import gomobi.io.forex.entity.UserEntity;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
	UserEntity registerUser(UserEntity user);
	SuccessResponse<UserResponseDTO> loginUser(String email, String password,HttpServletResponse response);
	boolean updateUserPassword(String email, String newPassword);
	Optional<?> updateUserDetails(String email, String fullName, String mobileNumber, LocalDate dateOfBirth);
	Optional<UserProfileDto> updateBalance(String email, BigDecimal amount, String operation);
}
