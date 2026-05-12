package com.portfolio.auth.service;

import com.portfolio.auth.service.JwtBlacklistService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class JwtBlacklistServiceImpl implements JwtBlacklistService {

    private static final String BLACKLIST_PREFIX = "blacklist:";

    @Autowired
    @Qualifier("authRedisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void blacklistToken(String jti, long expiryTime) {

        String key = BLACKLIST_PREFIX + jti;

        redisTemplate.opsForValue().set(
                key,
                "blacklisted",
                expiryTime,
                TimeUnit.MILLISECONDS
        );
    }

    @Override
    public boolean isBlacklisted(String jti) {

        String key = BLACKLIST_PREFIX + jti;

        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}