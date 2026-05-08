package com.portfolio.pricefetcher.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "portfolio-service", fallback = PortfolioFeignClientFallback.class)
public interface PortfolioFeignClient {

    @GetMapping("/api/holdings/symbols")
    List<String> getAllActiveSymbols();
}
