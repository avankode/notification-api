package com.avaneesh.notifcation_api.response;

import org.springframework.http.HttpStatus;

public class NotificationResponse {
    private HttpStatus status;
    private String message;

    public NotificationResponse(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
