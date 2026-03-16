package com.avaneesh.notifcation_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public String healthCheck() {
        // Render will hit this URL to verify the app is alive
        return "Notification-api service is up and running !!!!!!!";
    }

}
