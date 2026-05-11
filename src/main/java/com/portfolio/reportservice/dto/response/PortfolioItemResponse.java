package com.portfolio.reportservice.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class PortfolioItemResponse {

    private Long id;

    private String stockSymbol;

    private Double quantity;

    private Double buyPrice;

    private String buyDate;

    private Double currentPrice;

    private Double currentValue;

    private Double gainLoss;

    private Double gainLossPercent;
}