package com.portfolio.portfolioservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@FeignClient(name = "price-fetcher-service", fallback = PriceFeignClientFallback.class)
public interface PriceFeignClient {

    @GetMapping("/api/prices/{symbol}")
    Map<String, Object> getCurrentPrice(@PathVariable String symbol);

    @PostMapping("/api/prices/batch")
    Map<String, BigDecimal> getBatchPrices(@RequestBody List<String> symbols);
}
