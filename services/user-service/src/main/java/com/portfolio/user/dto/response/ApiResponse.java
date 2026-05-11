package com.portfolio.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

/**
 * Standard API response wrapper used across all services.
 * Every controller response is wrapped in this.
 * Other services (portfolio, report, notification) expect this format
 * when they call user-service via Feign.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(

        int status,
        String message,
        T data,
        LocalDateTime timestamp

) {
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(200, message, data, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> created(T data, String message) {
        return new ApiResponse<>(201, message, data, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> error(int status, String message) {
        return new ApiResponse<>(status, message, null, LocalDateTime.now());
    }
}
