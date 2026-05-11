package com.portfolio.pricefetcher.config;

import com.portfolio.pricefetcher.constants.AppConstants;
import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String PRICE_QUEUE =
            "price.updated.queue";

    @PostConstruct
    public void init() {

        System.out.println("RABBIT CONFIG LOADED");
    }

    @Bean
    public Queue priceUpdatedQueue() {

        return QueueBuilder
                .durable(PRICE_QUEUE)
                .build();
    }

    @Bean
    public TopicExchange portfolioExchange() {

        return ExchangeBuilder
                .topicExchange(AppConstants.EXCHANGE_NAME)
                .durable(true)
                .build();
    }

    @Bean
    public Binding binding(
            Queue priceUpdatedQueue,
            TopicExchange portfolioExchange
    ) {

        return BindingBuilder
                .bind(priceUpdatedQueue)
                .to(portfolioExchange)
                .with(AppConstants.ROUTING_KEY_PRICE_UPDATED);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(
            ConnectionFactory connectionFactory
    ) {

        return new RabbitAdmin(connectionFactory);
    }


    @Bean
    public Jackson2JsonMessageConverter messageConverter() {

        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter
    ) {

        RabbitTemplate template =
                new RabbitTemplate(connectionFactory);

        template.setMessageConverter(converter);

        return template;
    }
}