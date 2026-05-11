package com.portfolio.reportservice.client;

import com.portfolio.reportservice.config.FeignConfig;
import com.portfolio.reportservice.dto.response.PortfolioItemResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(
        name = "portfolio-service",
        url = "http://localhost:8083",
        configuration = FeignConfig.class
)
public interface PortfolioClient {

    @GetMapping("/api/portfolios/{id}/holdings")
    List<PortfolioItemResponse> getPortfolioHoldings(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId
    );
}