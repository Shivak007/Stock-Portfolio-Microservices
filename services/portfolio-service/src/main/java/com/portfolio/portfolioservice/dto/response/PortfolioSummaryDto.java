package com.portfolio.portfolioservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PortfolioSummaryDto {
    private Long portfolioId;
    private String name;
    private BigDecimal totalInvestedValue;
    private BigDecimal totalCurrentValue;
    private BigDecimal totalGainLoss;
    private BigDecimal totalGainLossPercent;
    private int holdingCount;
    private String currency;
    private LocalDateTime asOfTime;
}
