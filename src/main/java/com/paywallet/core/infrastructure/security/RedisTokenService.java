package com.paywallet.core.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisTokenService {

    private final StringRedisTemplate redisTemplate;

    @org.springframework.beans.factory.annotation.Value("${application.features.redis.enabled:true}")
    private boolean redisEnabled;

    // In-memory fallback
    private final java.util.Map<String, String> localTokenStore = new java.util.concurrent.ConcurrentHashMap<>();

    // Prefix for Redis keys to avoid collisions
    private static final String TOKEN_PREFIX = "auth:token:";

    public void saveToken(String token, String email, Duration ttl) {
        if (!redisEnabled) {
            localTokenStore.put(token, email);
            // Note: TTL is not strictly enforced in simple map fallback, but acceptable for
            // MVP
            return;
        }
        redisTemplate.opsForValue().set(TOKEN_PREFIX + token, email, ttl);
    }

    public Optional<String> validateToken(String token) {
        if (!redisEnabled) {
            return Optional.ofNullable(localTokenStore.get(token));
        }
        String email = redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
        return Optional.ofNullable(email);
    }

    public void deleteToken(String token) {
        if (!redisEnabled) {
            localTokenStore.remove(token);
            return;
        }
        redisTemplate.delete(TOKEN_PREFIX + token);
    }
}
