package com.taskflow.auth_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String PREFIX = "blacklist:";

    public void blacklistToken(String token, long expirationMillis) {
        redisTemplate.opsForValue().set(
                PREFIX + token,
                "true",
                Duration.ofMillis(expirationMillis)
        );
    }

    public boolean isTokenBlacklisted(String token) {
        return redisTemplate.hasKey(PREFIX + token);
    }
}
