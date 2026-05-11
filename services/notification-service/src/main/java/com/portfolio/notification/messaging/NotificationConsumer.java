package com.portfolio.notification.messaging;

import com.portfolio.notification.dto.AlertTriggeredEvent;
import com.portfolio.notification.dto.DailySummaryEvent;
import com.portfolio.notification.dto.UserRegisteredEvent;
import com.portfolio.notification.enums.NotificationType;
import com.portfolio.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationService notificationService;

    @Bean
    public Queue userRegisteredQueue() {
        return QueueBuilder.durable("notif.user.registered.queue")
                .withArgument("x-dead-letter-exchange", "portfolio.dlx").build();
    }

    @Bean
    public Queue alertTriggeredQueue() {
        return QueueBuilder.durable("notif.alert.triggered.queue")
                .withArgument("x-dead-letter-exchange", "portfolio.dlx").build();
    }

    @Bean
    public Queue dailySummaryQueue() {
        return QueueBuilder.durable("notif.daily.summary.queue")
                .withArgument("x-dead-letter-exchange", "portfolio.dlx").build();
    }

    @Bean
    public Binding userRegisteredBinding(Queue userRegisteredQueue, TopicExchange portfolioExchange) {
        return BindingBuilder.bind(userRegisteredQueue).to(portfolioExchange).with("user.registered");
    }

    @Bean
    public Binding alertTriggeredBinding(Queue alertTriggeredQueue, TopicExchange portfolioExchange) {
        return BindingBuilder.bind(alertTriggeredQueue).to(portfolioExchange).with("alert.triggered");
    }

    @Bean
    public Binding dailySummaryBinding(Queue dailySummaryQueue, TopicExchange portfolioExchange) {
        return BindingBuilder.bind(dailySummaryQueue).to(portfolioExchange).with("report.daily_summary");
    }

    @RabbitListener(queues = "notif.user.registered.queue")
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("Received UserRegistered event for userId={}", event.getUserId());
        String subject = "Welcome to Stock Portfolio Monitor!";
        String body = String.format(
                "Hi %s,\n\nWelcome to Stock Portfolio Monitor! Your account has been created successfully.\n\nRegards,\nPortfolio Team",
                event.getFullName());
        notificationService.createAndSend(event.getUserId(), event.getEmail(),
                subject, body, NotificationType.WELCOME_EMAIL);
    }

    @RabbitListener(queues = "notif.alert.triggered.queue")
    public void handleAlertTriggered(AlertTriggeredEvent event) {
        log.info("Received AlertTriggered event for userId={}, symbol={}", event.getUserId(), event.getStockSymbol());
        String subject = String.format("Alert Triggered: %s %s", event.getStockSymbol(), event.getAlertType());
        String body = String.format(
                "Your price alert for %s has been triggered!\n\nCondition: %s\nTarget Price: %s\nCurrent Price: %s\nTriggered At: %s\n\nLog in to review your portfolio.",
                event.getStockSymbol(), event.getCondition(),
                event.getTargetPrice(), event.getCurrentPrice(), event.getTriggeredAt());
        notificationService.createAndSend(event.getUserId(), event.getEmail(),
                subject, body, NotificationType.ALERT_TRIGGERED);
    }

    @RabbitListener(queues = "notif.daily.summary.queue")
    public void handleDailySummary(DailySummaryEvent event) {
        log.info("Received DailySummaryReady event for userId={}", event.getUserId());
        String subject = "Your Daily Portfolio Summary";
        String body = String.format(
                "Hi %s,\n\nDaily summary:\n\nTotal Value: %s\nGain/Loss: %s (%s%%)\nGenerated At: %s",
                event.getFullName(), event.getTotalCurrentValue(),
                event.getTotalGainLoss(), event.getTotalGainLossPercent(), event.getGeneratedAt());
        notificationService.createAndSend(event.getUserId(), event.getEmail(),
                subject, body, NotificationType.DAILY_SUMMARY);
    }
}
