package com.portfolio.alertservice.strategy;

import com.portfolio.alertservice.entity.Alert;
import com.portfolio.alertservice.enums.AlertCondition;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PriceThresholdStrategy implements AlertEvaluationStrategy {

    @Override
    public boolean evaluate(Alert alert, BigDecimal currentPrice, BigDecimal portfolioLossPercent) {
        if (alert.getTargetPrice() == null || currentPrice == null) return false;

        if (alert.getCondition() == AlertCondition.ABOVE) {
            return currentPrice.compareTo(alert.getTargetPrice()) >= 0;
        } else {
            return currentPrice.compareTo(alert.getTargetPrice()) <= 0;
        }
    }
}
