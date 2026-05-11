package com.portfolio.pricefetcher.messaging;

import com.portfolio.pricefetcher.constants.AppConstants;
import com.portfolio.pricefetcher.dto.response.PriceUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriceEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishPriceUpdated(
            PriceUpdatedEvent event
    ) {

        rabbitTemplate.convertAndSend(
                AppConstants.EXCHANGE_NAME,
                AppConstants.ROUTING_KEY_PRICE_UPDATED,
                event
        );

        log.info(
                "Published PriceUpdated event for symbol: {}",
                event.getSymbol()
        );
    }
}
