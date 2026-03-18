package com.avaneesh.notifcation_api.service;

import com.avaneesh.notifcation_api.config.RabbitMQConfig;
import com.avaneesh.notifcation_api.dto.NotificationRequestDTO;
import com.avaneesh.notifcation_api.response.NotificationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final RabbitTemplate rabbitTemplate;
    private final RateLimitingService rateLimitingService;
    private final IdempotencyService idempotencyService;

    public NotificationResponse processNotification(String tenantId, NotificationRequestDTO request) {

        // 1️⃣ Rate limiting
        if (!rateLimitingService.isAllowed(tenantId)) {
            log.warn("🚨 RATE LIMIT EXCEEDED for tenant: {}", tenantId);
            return new NotificationResponse(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "429 Too Many Requests: You have exceeded your limit."
            );
        }

        // 2️⃣ Deduplication
        if (idempotencyService.isDuplicate(tenantId, request)) {
            log.warn("♻️ DUPLICATE DETECTED for tenant: {}", tenantId);
            return new NotificationResponse(
                    HttpStatus.OK,
                    "Duplicate request detected and safely ignored."
            );
        }

        // 3️⃣ Process request
        request.setTenantId(tenantId);

        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, request);

        log.info("✅ Notification queued for tenant: {}, recipient: {}", tenantId, request.getRecipient());

        return new NotificationResponse(
                HttpStatus.OK,
                "Notification accepted for processing!"
        );
    }
}