package com.portfolio.alertservice.messaging.publisher;

import com.portfolio.alertservice.entity.Alert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class AlertEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String alertTriggeredKey;

    public AlertEventPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${rabbitmq.exchange}") String exchange,
            @Value("${rabbitmq.routing-key.alert-triggered}") String alertTriggeredKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.alertTriggeredKey = alertTriggeredKey;
    }

    public void publishAlertTriggered(Alert alert) {
        Map<String, Object> event = new HashMap<>();
        event.put("alertId", alert.getId());
        event.put("userId", alert.getUserId());
        event.put("alertType", alert.getAlertType().name());
        event.put("stockSymbol", alert.getStockSymbol());
        event.put("targetPrice", alert.getTargetPrice());
        event.put("triggeredAt", LocalDateTime.now().toString());

        try {
            rabbitTemplate.convertAndSend(exchange, alertTriggeredKey, event);
            log.info("Published AlertTriggered event for alertId={}, userId={}", alert.getId(), alert.getUserId());
        } catch (Exception e) {
            log.error("Failed to publish AlertTriggered for alertId={}: {}", alert.getId(), e.getMessage());
        }
    }
}
