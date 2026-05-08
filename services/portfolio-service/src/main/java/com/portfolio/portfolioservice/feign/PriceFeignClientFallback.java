package com.portfolio.portfolioservice.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class PriceFeignClientFallback implements PriceFeignClient {

    @Override
    public Map<String, Object> getCurrentPrice(String symbol) {
        log.warn("Price fetcher service unavailable. Returning mock price for symbol: {}", symbol);
        return Map.of("symbol", symbol, "price", BigDecimal.ZERO, "source", "fallback");
    }

    @Override
    public Map<String, BigDecimal> getBatchPrices(List<String> symbols) {
        log.warn("Price fetcher service unavailable. Returning empty batch prices.");
        return Collections.emptyMap();
    }
}
