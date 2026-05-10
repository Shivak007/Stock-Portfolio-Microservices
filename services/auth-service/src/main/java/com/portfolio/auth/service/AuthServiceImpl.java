 package com.portfolio.auth.service;

import com.portfolio.auth.config.JwtUtil;
import com.portfolio.auth.dto.*;
import com.portfolio.auth.entity.RefreshToken;
import com.portfolio.auth.entity.Role;
import com.portfolio.auth.entity.User;
import com.portfolio.auth.repository.RefreshTokenRepository;
import com.portfolio.auth.repository.UserRepository;
import com.portfolio.auth.response.ApiResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.portfolio.auth.dto.LogoutRequestDto;


import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthServiceImpl(UserRepository userRepository,
                           JwtUtil jwtUtil,
                           PasswordEncoder passwordEncoder,
                           RefreshTokenRepository refreshTokenRepository) {

        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public ApiResponse<?> register(RegisterRequestDto dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            return new ApiResponse<>(false, "Email already exists", null);
        }

        User user = new User();

        user.setEmail(dto.getEmail());
        user.setFullName(dto.getFullName());

        // Password hashing
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        // Default role
        user.setRole(Role.USER);

        userRepository.save(user);

        return new ApiResponse<>(true, "User registered successfully", null);
    }

    @Override
    public ApiResponse<?> login(LoginRequestDto dto) {

        var userOpt = userRepository.findByEmail(dto.getEmail());

        if (userOpt.isEmpty()) {
            return new ApiResponse<>(false, "User not found", null);
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return new ApiResponse<>(false, "Invalid password", null);
        }

        // Generate JWT Access Token
        String accessToken = jwtUtil.generateToken(user.getEmail());

        // Generate Refresh Token
        String refreshTokenValue = UUID.randomUUID().toString();

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setToken(refreshTokenValue);
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));

        refreshTokenRepository.save(refreshToken);

        // Build response DTO
        AuthResponseDto response = new AuthResponseDto(
                accessToken,
                refreshTokenValue,
                "Bearer",
                3600,
                user.getId(),
                user.getEmail(),
                Set.of(user.getRole().name())
        );

        return new ApiResponse<>(true, "Login successful", response);
    }

    @Override
    public ApiResponse<?> getCurrentUser() {

        String email = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        var userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return new ApiResponse<>(false, "User not found", null);
        }

        User user = userOpt.get();

        UserResponseDto response = new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getFullName()
        );

        return new ApiResponse<>(
                true,
                "User details fetched successfully",
                response
        );
    }

    @Override
    public ApiResponse<?> refreshToken(RefreshTokenRequestDto dto) {

        var refreshTokenOpt =
                refreshTokenRepository.findByToken(dto.getRefreshToken());

        if (refreshTokenOpt.isEmpty()) {
            return new ApiResponse<>(false,
                    "Invalid refresh token",
                    null);
        }

        RefreshToken refreshToken = refreshTokenOpt.get();

        // Check revoked
        if (refreshToken.isRevoked()) {
            return new ApiResponse<>(false,
                    "Refresh token revoked",
                    null);
        }

        // Check expiry
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return new ApiResponse<>(false,
                    "Refresh token expired",
                    null);
        }

        User user = refreshToken.getUser();

        // Generate new access token
        String newAccessToken =
                jwtUtil.generateToken(user.getEmail());

        AuthResponseDto response =
                new AuthResponseDto(
                        newAccessToken,
                        refreshToken.getToken(),
                        "Bearer",
                        3600,
                        user.getId(),
                        user.getEmail(),
                        Set.of(user.getRole().name())
                );

        return new ApiResponse<>(
                true,
                "Access token refreshed successfully",
                response
        );
    }


    @Override
    public ApiResponse<?> logout(LogoutRequestDto dto) {

        var refreshTokenOpt =
                refreshTokenRepository.findByToken(dto.getRefreshToken());

        if (refreshTokenOpt.isEmpty()) {

            return new ApiResponse<>(
                    false,
                    "Invalid refresh token",
                    null
            );
        }

        RefreshToken refreshToken = refreshTokenOpt.get();

        // Revoke token
        refreshToken.setRevoked(true);

        refreshTokenRepository.save(refreshToken);

        return new ApiResponse<>(
                true,
                "Logout successful",
                null
        );
    }



}
