package com.portfolio.alertservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "portfolio-service", fallback = PortfolioFeignClientFallback.class)
public interface PortfolioFeignClient {

    @GetMapping("/api/portfolios/{id}/summary")
    Map<String, Object> getPortfolioSummary(@PathVariable Long id);
}
