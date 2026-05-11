package com.portfolio.pricefetcher.exception;

import com.portfolio.pricefetcher.exception.custom.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.validation.ConstraintViolationException;
import com.portfolio.pricefetcher.exception.custom.ExternalServiceException;
import java.util.Map;
import java.util.stream.Collectors;
import com.portfolio.pricefetcher.exception.custom.ExternalApiException;
import com.portfolio.pricefetcher.exception.custom.InvalidExternalResponseException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        return ResponseEntity.badRequest().body(new ErrorResponseDto(400, "Validation failed", errors));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(404).body(new ErrorResponseDto(404, ex.getMessage(), null));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgument(
            IllegalArgumentException ex
    ) {

        return ResponseEntity.badRequest()
                .body(
                        new ErrorResponseDto(
                                400,
                                ex.getMessage(),
                                null
                        )
                );
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraintViolation(
            ConstraintViolationException ex
    ) {

        return ResponseEntity.badRequest().body(
                new ErrorResponseDto(
                        400,
                        "Validation failed",
                        null
                )
        );
    }
    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponseDto> handleExternalService(
            ExternalServiceException ex
    ) {

        return ResponseEntity.status(503)
                .body(
                        new ErrorResponseDto(
                                503,
                                ex.getMessage(),
                                null
                        )
                );
    }
    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ErrorResponseDto> handleExternalApi(
            ExternalApiException ex
    ) {

        return ResponseEntity.status(502)
                .body(
                        new ErrorResponseDto(
                                502,
                                ex.getMessage(),
                                null
                        )
                );
    }

    @ExceptionHandler(InvalidExternalResponseException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidExternalResponse(
            InvalidExternalResponseException ex
    ) {

        return ResponseEntity.status(502)
                .body(
                        new ErrorResponseDto(
                                502,
                                ex.getMessage(),
                                null
                        )
                );
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity.internalServerError()
                .body(new ErrorResponseDto(500, "Internal server error", null));
    }

    public record ErrorResponseDto(int status, String message, Map<String, String> errors) {}
}
