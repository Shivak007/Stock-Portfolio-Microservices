package com.portfolio.user.dto.response;

import java.time.LocalDateTime;

/**
 * Returned by GET /api/users/profile and GET /api/users/{id}
 * The GET /api/users/{id} endpoint is called by other services via Feign
 * (e.g. portfolio-service validates user exists before creating portfolio).
 * Keep this DTO stable — other services depend on it.
 */
public record UserProfileResponse(

        Long userId,
        String fullName,
        String email,
        String phone,
        String timezone,
        String currency,
        LocalDateTime createdAt

) {}
