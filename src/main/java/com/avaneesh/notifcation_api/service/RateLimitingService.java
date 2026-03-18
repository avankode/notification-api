package com.avaneesh.notifcation_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RateLimitingService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final int MAX_REQUESTS_PER_MINUTE = 5;

    public boolean isAllowed(String tenantId) {
        String key = "rate_limit:" + tenantId;

        Long currentRequests = redisTemplate.opsForValue().increment(key);

        if (currentRequests != null && currentRequests == 1) {
            redisTemplate.expire(key, 1, TimeUnit.MINUTES);
        }

        return currentRequests != null && currentRequests <= MAX_REQUESTS_PER_MINUTE;
    }
}
