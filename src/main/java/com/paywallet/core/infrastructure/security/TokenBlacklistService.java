package com.paywallet.core.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final StringRedisTemplate redisTemplate;
    private final JwtService jwtService;

    public void blacklistToken(String token) {
        String username = jwtService.extractUsername(token);
        // Calculate remaining time for the token
        // For simplicity, we just use the fixed expiration from config or we can parse
        // it
        // Ideally we parse expiration from token
        long expirationTime = jwtService.extractClaim(token, claims -> claims.getExpiration().getTime());
        long remainingTime = expirationTime - System.currentTimeMillis();

        if (remainingTime > 0) {
            String key = "blacklist:token:" + token;
            redisTemplate.opsForValue().set(key, username, Duration.ofMillis(remainingTime));
        }
    }

    public boolean isBlacklisted(String token) {
        String key = "blacklist:token:" + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
