 package com.portfolio.auth.service;

import com.portfolio.auth.config.JwtUtil;
import com.portfolio.auth.dto.*;
import com.portfolio.auth.entity.RefreshToken;
import com.portfolio.auth.entity.Role;
import com.portfolio.auth.entity.User;
import com.portfolio.auth.enums.AuthProvider;
import com.portfolio.auth.repository.RefreshTokenRepository;
import com.portfolio.auth.repository.UserRepository;
import com.portfolio.auth.response.ApiResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.portfolio.auth.dto.LogoutRequestDto;

import com.portfolio.auth.messaging.UserEventPublisher;
import com.portfolio.auth.messaging.UserRegisteredEvent;

import com.portfolio.auth.service.JwtBlacklistService;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtBlacklistService jwtBlacklistService;
    private final UserEventPublisher userEventPublisher;

    public AuthServiceImpl(UserRepository userRepository,
                           JwtUtil jwtUtil,
                           PasswordEncoder passwordEncoder,
                           RefreshTokenRepository refreshTokenRepository,
                           JwtBlacklistService jwtBlacklistService,
                           UserEventPublisher userEventPublisher) {

        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtBlacklistService = jwtBlacklistService;
        this.userEventPublisher = userEventPublisher;
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
        user.setProvider(AuthProvider.LOCAL);

        User savedUser = userRepository.save(user);

        UserRegisteredEvent event =
                new UserRegisteredEvent(
                        savedUser.getId(),
                        savedUser.getEmail(),
                        savedUser.getRole().name()
                );

        userEventPublisher.publishUserRegisteredEvent(event);

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

        // Revoke refresh token
        refreshToken.setRevoked(true);

        refreshTokenRepository.save(refreshToken);

        try {

            String authHeader =
                    org.springframework.security.core.context.SecurityContextHolder
                            .getContext()
                            .getAuthentication()
                            .getCredentials()
                            .toString();

            String token = authHeader.replace("Bearer ", "");

            // Extract jti
            String jti = jwtUtil.extractJti(token);

            // Remaining expiry time
            long remainingTime =
                    jwtUtil.getRemainingValidity(token);

            // Blacklist token
            jwtBlacklistService.blacklistToken(jti, remainingTime);

        } catch (Exception e) {

            System.out.println("JWT blacklist failed: " + e.getMessage());
        }

        return new ApiResponse<>(
                true,
                "Logout successful",
                null
        );
    }



}
