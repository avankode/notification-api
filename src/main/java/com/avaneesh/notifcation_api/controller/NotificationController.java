package com.avaneesh.notifcation_api.controller;

import com.avaneesh.notifcation_api.config.RabbitMQConfig;
import com.avaneesh.notifcation_api.dto.NotificationRequestDTO;
import com.avaneesh.notifcation_api.response.NotificationResponse;
import com.avaneesh.notifcation_api.service.IdempotencyService;
import com.avaneesh.notifcation_api.service.NotificationService;
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

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequestDTO request, HttpServletRequest httpRequest) {

        String tenantId = (String) httpRequest.getAttribute("tenantId");
        NotificationResponse response =
                notificationService.processNotification(tenantId, request);
        return ResponseEntity
                .status(response.getStatus())
                .body(response.getMessage());
    }
}
