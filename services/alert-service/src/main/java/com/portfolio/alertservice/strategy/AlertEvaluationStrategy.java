package com.portfolio.alertservice.strategy;

import com.portfolio.alertservice.entity.Alert;

import java.math.BigDecimal;

public interface AlertEvaluationStrategy {
    boolean evaluate(Alert alert, BigDecimal currentPrice, BigDecimal portfolioLossPercent);
}
