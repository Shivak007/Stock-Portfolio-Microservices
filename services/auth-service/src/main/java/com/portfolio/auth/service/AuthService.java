 package com.portfolio.auth.service;

import com.portfolio.auth.dto.LoginRequestDto;
import com.portfolio.auth.dto.RefreshTokenRequestDto;
import com.portfolio.auth.dto.RegisterRequestDto;
import com.portfolio.auth.response.ApiResponse;
import com.portfolio.auth.dto.LogoutRequestDto;
public interface AuthService {

    ApiResponse<?> register(RegisterRequestDto dto);
    ApiResponse<?> login(LoginRequestDto dto);
    ApiResponse<?> getCurrentUser();
    ApiResponse<?> refreshToken(RefreshTokenRequestDto dto);
    ApiResponse<?> logout(LogoutRequestDto dto);




}

