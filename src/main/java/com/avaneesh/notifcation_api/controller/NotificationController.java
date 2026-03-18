package com.avaneesh.notifcation_api.controller;

import com.avaneesh.notifcation_api.config.RabbitMQConfig;
import com.avaneesh.notifcation_api.dto.NotificationRequestDTO;
import com.avaneesh.notifcation_api.service.IdempotencyService;
import com.avaneesh.notifcation_api.service.RateLimitingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final RabbitTemplate rabbitTemplate;
    private final RateLimitingService rateLimitingService;
    private final IdempotencyService idempotencyService;

    public NotificationController(RabbitTemplate rabbitTemplate, RateLimitingService rateLimitingService, IdempotencyService idempotencyService) {
        this.idempotencyService = idempotencyService;
        this.rabbitTemplate = rabbitTemplate;
        this.rateLimitingService = rateLimitingService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequestDTO request , HttpServletRequest httpRequest
    , @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {

        String tenantId = (String) httpRequest.getAttribute("tenantId");

        if (!rateLimitingService.isAllowed(tenantId)){
            log.warn("🚨 RATE LIMIT EXCEEDED for tenant: " + tenantId);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("429 Too Many Requests: You have exceeded your limit of 5 notifications per minute.");
        }

        if (idempotencyService.isDuplicate(idempotencyKey)) {
            System.out.println("♻️ DUPLICATE DETECTED: Ignoring request with key " + idempotencyKey);
            return ResponseEntity.ok("Duplicate request detected and safely ignored.");
        }

        request.setTenantId(tenantId);
        log.info("Received request from tenant: {}" , tenantId);

        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, request);
        log.info("Successfully queued notification for: {}",  request.getRecipient());
        return ResponseEntity.ok("Notification accepted for processing!");
    }
}
