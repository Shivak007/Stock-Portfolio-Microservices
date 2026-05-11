package com.portfolio.reportservice.util;

import com.portfolio.reportservice.dto.response.PortfolioItemResponse;

import java.util.List;

public class DummyPortfolioData {

    public static List<PortfolioItemResponse> getPortfolioData() {

        return List.of(

                PortfolioItemResponse.builder()
                        .stockSymbol("TCS")
                        .quantity(10.0)
                        .buyPrice(3200.0)
                        .currentPrice(3500.0)
                        .gainLoss(3000.0)
                        .build(),

                PortfolioItemResponse.builder()
                        .stockSymbol("Infosys")
                        .quantity(5.0)
                        .buyPrice(1400.0)
                        .currentPrice(1550.0)
                        .gainLoss(750.0)
                        .build(),

                PortfolioItemResponse.builder()
                        .stockSymbol("HDFC")
                        .quantity(8.0)
                        .buyPrice(1500.0)
                        .currentPrice(1700.0)
                        .gainLoss(1600.0)
                        .build()
        );
    }
}