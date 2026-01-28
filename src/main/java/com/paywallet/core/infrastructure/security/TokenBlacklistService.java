package com.paywallet.core.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final StringRedisTemplate redisTemplate;
    private final JwtService jwtService;
    private final Map<String, Long> localBlacklist = new ConcurrentHashMap<>();

    @Value("${application.features.redis.enabled:true}")
    private boolean redisEnabled;

    public void blacklistToken(String token) {
        String username = jwtService.extractUsername(token);
        long expirationTime = jwtService.extractClaim(token, claims -> claims.getExpiration().getTime());
        long remainingTime = expirationTime - System.currentTimeMillis();

        if (remainingTime > 0) {
            if (!redisEnabled) {
                localBlacklist.put("blacklist:token:" + token, expirationTime);
                return;
            }
            String key = "blacklist:token:" + token;
            redisTemplate.opsForValue().set(key, username, Duration.ofMillis(remainingTime));
        }
    }

    public boolean isBlacklisted(String token) {
        String key = "blacklist:token:" + token;
        if (!redisEnabled) {
            Long expiration = localBlacklist.get(key);
            if (expiration == null)
                return false;
            if (expiration < System.currentTimeMillis()) {
                localBlacklist.remove(key);
                return false;
            }
            return true;
        }
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
