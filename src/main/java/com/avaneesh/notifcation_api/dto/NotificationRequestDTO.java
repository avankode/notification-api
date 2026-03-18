package com.avaneesh.notifcation_api.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequestDTO implements Serializable {

    private String webhookUrl;
    private String tenantId;
    private String channel;
    private String recipient;
    private String subject;
    private String messageBody;

    @Override
    public String toString() {
        return "To: " + recipient + " | Subject: " + subject;
    }
}