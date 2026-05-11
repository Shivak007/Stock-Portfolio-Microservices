package com.portfolio.user.dto.response;

/**
 * Returned by GET /api/users/preferences
 * Also consumed by notification-service via Feign to check
 * whether the user wants email notifications before sending.
 */
public record UserPreferenceResponse(

        Long userId,
        boolean emailNotifications,
        boolean priceAlertEmail,
        boolean dailySummaryEmail,
        String preferredCurrency

) {}
