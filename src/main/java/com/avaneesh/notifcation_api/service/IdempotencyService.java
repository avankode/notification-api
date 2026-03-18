package com.avaneesh.notifcation_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class IdempotencyService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final int DEDUPE_WINDOW_MINUTES = 10;

    /**
     * Checks if a request with this key has been seen recently.
     * @return true if it is a duplicate, false if it is a new request.
     */
    public boolean isDuplicate(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return false;
        }
        String redisKey = "idempotency:" + idempotencyKey;
        Boolean isNewRequest = redisTemplate.opsForValue().setIfAbsent(redisKey, "processed", DEDUPE_WINDOW_MINUTES, TimeUnit.MINUTES);
        return !Boolean.TRUE.equals(isNewRequest);
    }
}