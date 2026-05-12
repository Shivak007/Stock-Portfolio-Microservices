package com.portfolio.auth.messaging;

import com.portfolio.auth.config.RabbitMQConfig;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.stereotype.Service;

@Service
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public UserEventPublisher(
            RabbitTemplate rabbitTemplate
    ) {

        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishUserRegisteredEvent(
            UserRegisteredEvent event
    ) {

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.USER_REGISTERED_KEY,
                event
        );

        System.out.println(
                "User Registered Event Published: "
                        + event.getEmail()
        );
    }
}