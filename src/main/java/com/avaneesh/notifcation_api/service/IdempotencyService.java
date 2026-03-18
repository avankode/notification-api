package com.avaneesh.notifcation_api.service;

import com.avaneesh.notifcation_api.dto.NotificationRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Service
public class IdempotencyService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final int DEDUPE_WINDOW_MINUTES = 10;

    public boolean isDuplicate(String tenantId, NotificationRequestDTO request) {
        String contentHash = generateContentHash(tenantId, request);
        String redisKey = "dedupe:" + contentHash;

        Boolean isNewRequest = redisTemplate.opsForValue()
                .setIfAbsent(redisKey, "processed", DEDUPE_WINDOW_MINUTES, TimeUnit.MINUTES);

        return !Boolean.TRUE.equals(isNewRequest);
    }


    private String generateContentHash(String tenantId, NotificationRequestDTO request) {
        try {
            String rawString = String.join("|",
                    tenantId,
                    request.getChannel(),
                    request.getRecipient(),
                    request.getSubject() != null ? request.getSubject() : "",
                    request.getMessageBody()
            );

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawString.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unable to generate payload hash", e);
        }
    }
}