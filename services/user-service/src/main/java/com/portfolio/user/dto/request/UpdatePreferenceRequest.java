package com.portfolio.user.dto.request;

import jakarta.validation.constraints.Size;

/**
 * Used by PUT /api/users/preferences
 * All fields are optional — partial update supported.
 */
public record UpdatePreferenceRequest(

        Boolean emailNotifications,

        Boolean priceAlertEmail,

        Boolean dailySummaryEmail,

        @Size(max = 10, message = "Currency code must not exceed 10 characters")
        String preferredCurrency

) {}
