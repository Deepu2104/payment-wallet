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

    // Prefix for Redis keys to avoid collisions
    private static final String TOKEN_PREFIX = "auth:token:";

    public void saveToken(String token, String email, Duration ttl) {
        redisTemplate.opsForValue().set(TOKEN_PREFIX + token, email, ttl);
    }

    public Optional<String> validateToken(String token) {
        String email = redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
        return Optional.ofNullable(email);
    }

    public void deleteToken(String token) {
        redisTemplate.delete(TOKEN_PREFIX + token);
    }
}
