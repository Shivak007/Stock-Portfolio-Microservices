package com.portfolio.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Used by POST /api/users/internal/create
 * Called by auth-service (via Feign) right after a user registers.
 * This creates the user profile record in user_db.
 */
public record CreateUserProfileRequest(

        @NotNull(message = "userId must not be null")
        Long userId,

        @NotBlank(message = "Full name must not be blank")
        @Size(min = 2, max = 100)
        String fullName,

        @NotBlank(message = "Email must not be blank")
        @Email(message = "Invalid email format")
        String email

) {}
