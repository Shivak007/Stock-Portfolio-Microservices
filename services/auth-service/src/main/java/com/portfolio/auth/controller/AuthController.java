package com.portfolio.auth.controller;

import com.portfolio.auth.dto.LoginRequestDto;
import com.portfolio.auth.dto.RefreshTokenRequestDto;
import com.portfolio.auth.dto.RegisterRequestDto;
import com.portfolio.auth.response.ApiResponse;
import com.portfolio.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


import com.portfolio.auth.dto.LogoutRequestDto;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<?> register(@Valid @RequestBody RegisterRequestDto dto) {
        return authService.register(dto);
    }
    @PostMapping("/login")
    public ApiResponse<?> login(@Valid @RequestBody LoginRequestDto dto) {
        return authService.login(dto);
    }
    @GetMapping("/me")
    public ApiResponse<?> getCurrentUser() {
        return authService.getCurrentUser();
    }


    @GetMapping("/user")
    public String userEndpoint() {
        return "Hello USER";
    }

    @GetMapping("/admin")
    public String adminEndpoint() {
        return "Hello ADMIN";
    }


    @PostMapping("/refresh")
    public ApiResponse<?> refreshToken(
            @Valid @RequestBody RefreshTokenRequestDto dto) {

        return authService.refreshToken(dto);
    }

    @PostMapping("/logout")
    public ApiResponse<?> logout(
            @Valid @RequestBody LogoutRequestDto dto) {

        return authService.logout(dto);
    }








}

