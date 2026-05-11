package com.portfolio.reportservice.generator;

import com.portfolio.reportservice.dto.response.PortfolioResponse;

import java.io.ByteArrayInputStream;

public interface ReportGenerator {

    ByteArrayInputStream generateReport(
            PortfolioResponse portfolio
    );
}