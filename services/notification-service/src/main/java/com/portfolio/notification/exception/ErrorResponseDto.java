package com.portfolio.notification.exception;

import lombok.*;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ErrorResponseDto {
    private int status;
    private String message;
    private Map<String, String> errors;
}
