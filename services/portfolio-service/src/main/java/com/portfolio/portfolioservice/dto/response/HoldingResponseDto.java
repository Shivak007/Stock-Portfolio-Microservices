package com.portfolio.portfolioservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class HoldingResponseDto {
    private Long id;
    private String stockSymbol;
    private BigDecimal quantity;
    private BigDecimal buyPrice;
    private LocalDate buyDate;
    private BigDecimal currentPrice;
    private BigDecimal currentValue;
    private BigDecimal gainLoss;
    private BigDecimal gainLossPercent;
}
