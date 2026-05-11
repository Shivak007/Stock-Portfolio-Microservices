package com.portfolio.reportservice.service.impl;

import com.portfolio.reportservice.client.PortfolioClient;
import com.portfolio.reportservice.dto.response.PortfolioResponse;
import com.portfolio.reportservice.factory.ReportFactory;
import com.portfolio.reportservice.generator.ReportGenerator;
import com.portfolio.reportservice.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportFactory reportFactory;

    private final PortfolioClient portfolioClient;

    @Override
    public ByteArrayInputStream generateReport(
            String type,
            Long userId
    ) {

        PortfolioResponse portfolio = new PortfolioResponse();

        portfolio.setUserId(userId);

        portfolio.setStocks(
                portfolioClient.getPortfolioHoldings(
                        userId,
                        userId
                )
        );

        double totalInvestment = portfolio.getStocks()
                .stream()
                .mapToDouble(item ->
                        item.getBuyPrice() * item.getQuantity()
                )
                .sum();

        double currentValue = portfolio.getStocks()
                .stream()
                .mapToDouble(item ->
                        item.getCurrentValue()
                )
                .sum();

        double totalProfit = portfolio.getStocks()
                .stream()
                .mapToDouble(item ->
                        item.getGainLoss()
                )
                .sum();

        portfolio.setTotalInvestment(totalInvestment);

        portfolio.setCurrentValue(currentValue);

        portfolio.setTotalProfit(totalProfit);

        ReportGenerator generator =
                reportFactory.getReportGenerator(type);

        return generator.generateReport(portfolio);
    }
}