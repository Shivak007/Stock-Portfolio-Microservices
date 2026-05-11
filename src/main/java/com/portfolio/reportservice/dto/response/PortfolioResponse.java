package com.portfolio.reportservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioResponse {

    private Long userId;

    private List<PortfolioItemResponse> stocks;

    private double totalInvestment;

    private double currentValue;

    private double totalProfit;
}