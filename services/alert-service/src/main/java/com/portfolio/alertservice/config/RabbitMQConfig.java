package com.portfolio.alertservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.queues.price-updated}")
    private String alertQueue;

    @Bean
    public TopicExchange portfolioExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange("portfolio.dlx");
    }

    @Bean
    public Queue alertQueue() {
        return QueueBuilder.durable(alertQueue)
                .withArgument("x-dead-letter-exchange", "portfolio.dlx")
                .build();
    }

    @Bean
    public Binding alertBinding(Queue alertQueue, TopicExchange portfolioExchange) {
        return BindingBuilder.bind(alertQueue).to(portfolioExchange).with("price.updated");
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
