package com.portfolio.auth.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange
    public static final String EXCHANGE =
            "portfolio.exchange";

    // Queue
    public static final String USER_REGISTERED_QUEUE =
            "user.registered.queue";

    // Routing Key
    public static final String USER_REGISTERED_KEY =
            "user.registered";

    @Bean
    public TopicExchange portfolioExchange() {

        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue userRegisteredQueue() {

        return new Queue(USER_REGISTERED_QUEUE);
    }

    @Bean
    public Binding userRegisteredBinding(
            Queue userRegisteredQueue,
            TopicExchange portfolioExchange
    ) {

        return BindingBuilder
                .bind(userRegisteredQueue)
                .to(portfolioExchange)
                .with(USER_REGISTERED_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {

        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory
    ) {

        RabbitTemplate template =
                new RabbitTemplate(connectionFactory);

        template.setMessageConverter(messageConverter());

        return template;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(
            ConnectionFactory connectionFactory
    ) {

        RabbitAdmin admin =
                new RabbitAdmin(connectionFactory);

        admin.setAutoStartup(true);

        return admin;
    }

    @jakarta.annotation.PostConstruct
    public void init() {
        System.out.println("RabbitMQConfig Loaded");
    }
}