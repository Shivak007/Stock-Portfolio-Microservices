package com.portfolio.pricefetcher.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class PortfolioFeignClientFallback implements PortfolioFeignClient {

    @Override
    public List<String> getAllActiveSymbols() {
        log.warn("Fallback: portfolio-service unavailable. Returning empty symbol list.");
        return Collections.emptyList();
    }
}
