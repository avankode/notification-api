package com.avaneesh.notifcation_api.controller;

import com.avaneesh.notifcation_api.config.RabbitMQConfig;
import com.avaneesh.notifcation_api.dto.NotificationRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final RabbitTemplate rabbitTemplate;

    // Spring automatically injects the RabbitTemplate here
    public NotificationController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping("/send")
    public String sendNotification(@RequestBody NotificationRequestDTO request , HttpServletRequest httpRequest) {
        // We are now sending the whole object!
        String tenantId = (String) httpRequest.getAttribute("tenantId");
        request.setTenantId(tenantId);
        log.info("Received request from tenant: {}" , tenantId);

        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, request);
        log.info("Successfully queued notification for: {}",  request.getRecipient());
        return "Successfully queued notification for: " + request.getRecipient();
    }
}
