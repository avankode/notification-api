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

    private String recipientEmail;
    private String subject;
    private String messageBody;

    @Override
    public String toString() {
        return "To: " + recipientEmail + " | Subject: " + subject;
    }
}