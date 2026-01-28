package com.paywallet.core.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {

    private final StringRedisTemplate redisTemplate;
    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    private final Map<String, AtomicLong> localRateLimits = new ConcurrentHashMap<>();
    private final Map<String, Long> localRateLimitExpiries = new ConcurrentHashMap<>();

    @Value("${application.features.redis.enabled:true}")
    private boolean redisEnabled;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String clientIp = request.getRemoteAddr();
        String key = "rate_limit:" + clientIp;
        Long count;

        if (!redisEnabled) {
            long now = System.currentTimeMillis();
            Long expiry = localRateLimitExpiries.get(key);

            if (expiry == null || expiry < now) {
                localRateLimits.put(key, new AtomicLong(1));
                localRateLimitExpiries.put(key, now + 60000); // 1 minute
                count = 1L;
            } else {
                count = localRateLimits.get(key).incrementAndGet();
            }
        } else {
            count = redisTemplate.opsForValue().increment(key);
            if (count != null && count == 1) {
                redisTemplate.expire(key, Duration.ofMinutes(1));
            }
        }

        if (count != null && count > MAX_REQUESTS_PER_MINUTE) {
            response.setStatus(429); // Too Many Requests
            response.getWriter().write("Too many requests");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
