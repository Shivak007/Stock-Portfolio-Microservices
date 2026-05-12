package com.portfolio.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class AuthRedisConfig {

    @Value("${auth.redis.host}")
    private String redisHost;

    @Value("${auth.redis.port}")
    private int redisPort;

    @Bean(name = "authRedisConnectionFactory")
    public RedisConnectionFactory authRedisConnectionFactory() {

        return new LettuceConnectionFactory(redisHost, redisPort);
    }

    @Bean(name = "authRedisTemplate")
    public RedisTemplate<String, Object> authRedisTemplate(
            @Qualifier("authRedisConnectionFactory")
            RedisConnectionFactory connectionFactory
    ) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());

        template.afterPropertiesSet();

        return template;
    }
}