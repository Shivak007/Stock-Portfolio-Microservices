
package com.portfolio.auth.service;

public interface TokenBlacklistService {

    void blacklistToken(String jti, long expiryMillis);

    boolean isBlacklisted(String jti);
}
