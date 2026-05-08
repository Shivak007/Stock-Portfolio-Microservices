package com.portfolio.alertservice.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@Slf4j
public class PortfolioFeignClientFallback implements PortfolioFeignClient {

    @Override
    public Map<String, Object> getPortfolioSummary(Long id) {
        log.warn("Portfolio service unavailable. Cannot fetch summary for portfolioId={}", id);
        return Collections.emptyMap();
    }
}
