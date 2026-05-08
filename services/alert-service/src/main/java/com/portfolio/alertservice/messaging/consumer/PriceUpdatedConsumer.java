package com.portfolio.alertservice.messaging.consumer;

import com.portfolio.alertservice.entity.Alert;
import com.portfolio.alertservice.enums.AlertStatus;
import com.portfolio.alertservice.enums.AlertType;
import com.portfolio.alertservice.feign.PortfolioFeignClient;
import com.portfolio.alertservice.messaging.publisher.AlertEventPublisher;
import com.portfolio.alertservice.repository.AlertRepository;
import com.portfolio.alertservice.strategy.AlertEvaluationStrategy;
import com.portfolio.alertservice.strategy.PortfolioLossStrategy;
import com.portfolio.alertservice.strategy.PriceThresholdStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class PriceUpdatedConsumer {

    private final AlertRepository alertRepository;
    private final PortfolioFeignClient portfolioFeignClient;
    private final AlertEventPublisher alertEventPublisher;
    private final PriceThresholdStrategy priceThresholdStrategy;
    private final PortfolioLossStrategy portfolioLossStrategy;

    public PriceUpdatedConsumer(
            AlertRepository alertRepository,
            PortfolioFeignClient portfolioFeignClient,
            AlertEventPublisher alertEventPublisher,
            PriceThresholdStrategy priceThresholdStrategy,
            PortfolioLossStrategy portfolioLossStrategy) {
        this.alertRepository = alertRepository;
        this.portfolioFeignClient = portfolioFeignClient;
        this.alertEventPublisher = alertEventPublisher;
        this.priceThresholdStrategy = priceThresholdStrategy;
        this.portfolioLossStrategy = portfolioLossStrategy;
    }

    @RabbitListener(queues = "${rabbitmq.queues.price-updated}")
    @Transactional
    public void onPriceUpdated(Map<String, Object> event) {
        try {
            String symbol = (String) event.get("symbol");
            BigDecimal currentPrice = new BigDecimal(event.get("currentPrice").toString());
            log.debug("Received PriceUpdated for symbol={}, price={}", symbol, currentPrice);

            // Find all ACTIVE alerts for this symbol
            List<Alert> alerts = alertRepository.findByStockSymbolAndStatus(symbol, AlertStatus.ACTIVE);

            for (Alert alert : alerts) {
                boolean shouldFire = false;

                if (alert.getAlertType() == AlertType.PRICE_THRESHOLD) {
                    shouldFire = priceThresholdStrategy.evaluate(alert, currentPrice, null);
                } else if (alert.getAlertType() == AlertType.PORTFOLIO_LOSS_PERCENT && alert.getPortfolioId() != null) {
                    Map<String, Object> summary = portfolioFeignClient.getPortfolioSummary(alert.getPortfolioId());
                    BigDecimal lossPercent = summary.containsKey("totalGainLossPercent")
                            ? new BigDecimal(summary.get("totalGainLossPercent").toString())
                            : null;
                    shouldFire = portfolioLossStrategy.evaluate(alert, currentPrice, lossPercent);
                }

                if (shouldFire) {
                    alert.setStatus(AlertStatus.TRIGGERED);
                    alert.setLastTriggeredAt(LocalDateTime.now());
                    alertRepository.save(alert);
                    alertEventPublisher.publishAlertTriggered(alert);
                    log.info("Alert TRIGGERED: alertId={}, userId={}, symbol={}", alert.getId(), alert.getUserId(), symbol);
                }
            }
        } catch (Exception e) {
            log.error("Error processing PriceUpdated event: {}", e.getMessage(), e);
        }
    }
}
