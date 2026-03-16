package com.avaneesh.notifcation_api.controller;

import com.avaneesh.notifcation_api.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final RabbitTemplate rabbitTemplate;

    // Spring automatically injects the RabbitTemplate here
    public NotificationController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping("/send")
    public String sendNotification(@RequestBody String message) {
        // Send the message payload exactly to our named queue
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, message);
        return "Successfully dropped message into queue: " + message;
    }

}
