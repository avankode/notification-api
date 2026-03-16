package com.avaneesh.notifcation_api.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "notification_queue";

    @Bean
    public Queue notificationQueue() {
        // The 'true' means the queue is durable (it survives server restarts)
        return new Queue(QUEUE_NAME, true);
    }
}
