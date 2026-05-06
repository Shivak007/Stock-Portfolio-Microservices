package com.portfolio.portfolioservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class HoldingRequestDto {

    @NotNull(message = "Portfolio ID is required")
    private Long portfolioId;

    @NotBlank(message = "Stock symbol is required")
    @Size(max = 10, message = "Symbol must be at most 10 characters")
    private String stockSymbol;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private BigDecimal quantity;

    @NotNull(message = "Buy price is required")
    @Positive(message = "Buy price must be positive")
    private BigDecimal buyPrice;

    @PastOrPresent(message = "Buy date cannot be in the future")
    private LocalDate buyDate;
}
