package com.portfolio.alertservice.strategy;

import com.portfolio.alertservice.entity.Alert;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PortfolioLossStrategy implements AlertEvaluationStrategy {

    @Override
    public boolean evaluate(Alert alert, BigDecimal currentPrice, BigDecimal portfolioLossPercent) {
        if (alert.getLossThresholdPercent() == null || portfolioLossPercent == null) return false;
        // Fire if loss exceeds the threshold (portfolioLossPercent is negative for losses)
        return portfolioLossPercent.compareTo(alert.getLossThresholdPercent().negate()) <= 0;
    }
}
