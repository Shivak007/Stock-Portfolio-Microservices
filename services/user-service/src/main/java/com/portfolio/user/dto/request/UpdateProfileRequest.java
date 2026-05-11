package com.portfolio.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Used by PUT /api/users/profile
 * All fields are optional — only provided fields are updated.
 * Email is NOT updatable here — email is owned by auth-service.
 */
public record UpdateProfileRequest(

        @NotBlank(message = "Full name must not be blank")
        @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
        String fullName,

        @Pattern(regexp = "^[+]?[0-9]{7,15}$", message = "Invalid phone number format")
        String phone,

        @Size(max = 50, message = "Timezone must not exceed 50 characters")
        String timezone,

        @Size(max = 10, message = "Currency code must not exceed 10 characters")
        String currency

) {}
