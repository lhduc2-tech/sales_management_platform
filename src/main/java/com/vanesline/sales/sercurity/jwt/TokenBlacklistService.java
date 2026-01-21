package com.vanesline.sales.sercurity.jwt;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistService {

    private static final String BLACKLIST_PREFIX = "blacklist:token:";

    private final StringRedisTemplate redisTemplate;
    private final JwtTokenService jwtTokenService;

    public TokenBlacklistService(StringRedisTemplate redisTemplate, JwtTokenService jwtTokenService) {
        this.redisTemplate = redisTemplate;
        this.jwtTokenService = jwtTokenService;
    }

    /**
     * Add a token to the blacklist with TTL equal to the token's remaining lifetime
     */
    public void blacklist(String token) {
        long ttlMillis = jwtTokenService.getRemainingTtlMillis(token);
        if (ttlMillis > 0) {
            String key = BLACKLIST_PREFIX + token;
            redisTemplate.opsForValue().set(key, "1", ttlMillis, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Check if a token is blacklisted
     */
    public boolean isBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        return redisTemplate.hasKey(key);
    }
}
