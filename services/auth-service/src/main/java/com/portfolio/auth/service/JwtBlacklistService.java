package com.portfolio.auth.service;

public interface JwtBlacklistService {

    void blacklistToken(String jti, long expiryTime);

    boolean isBlacklisted(String jti);
}