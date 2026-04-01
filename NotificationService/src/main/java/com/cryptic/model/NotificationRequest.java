package com.cryptic.model;

public record NotificationRequest(
        String recipient,     // email or phone
        String channel,       // "EMAIL" or "SMS"
        String subject,
        String body
) {}
