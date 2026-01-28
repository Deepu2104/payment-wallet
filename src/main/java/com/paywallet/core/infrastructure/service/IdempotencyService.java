package com.paywallet.core.infrastructure.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final StringRedisTemplate redisTemplate;
    private static final long EXPIRATION_HOURS = 24;
    private final Map<String, Long> localLocks = new ConcurrentHashMap<>();

    @Value("${application.features.redis.enabled:true}")
    private boolean redisEnabled;

    public boolean lock(String key) {
        if (!redisEnabled) {
            String lockKey = "idempotency:" + key;
            long now = System.currentTimeMillis();
            // Simple cleanup logic could be added here, but for demo it's fine
            return localLocks.putIfAbsent(lockKey, now) == null;
        }
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent("idempotency:" + key, "LOCKED", Duration.ofHours(EXPIRATION_HOURS));
        return Boolean.TRUE.equals(success);
    }

    public void unlock(String key) {
        if (!redisEnabled) {
            localLocks.remove("idempotency:" + key);
            return;
        }
        redisTemplate.delete("idempotency:" + key);
    }
}
