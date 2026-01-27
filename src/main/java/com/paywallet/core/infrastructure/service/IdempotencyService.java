package com.paywallet.core.infrastructure.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final StringRedisTemplate redisTemplate;
    private static final long EXPIRATION_HOURS = 24;

    public boolean lock(String key) {
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent("idempotency:" + key, "LOCKED", Duration.ofHours(EXPIRATION_HOURS));
        return Boolean.TRUE.equals(success);
    }

    public void unlock(String key) {
        redisTemplate.delete("idempotency:" + key);
    }
}
