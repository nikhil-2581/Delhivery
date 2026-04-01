package com.cryptic.client;

import com.cryptic.model.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class NotificationClient {
    private final RestTemplate restTemplate;
    private final String notificationUrl;

    public NotificationClient(RestTemplate restTemplate,
                              @Value("${notification.service.url}") String notificationUrl) {
        this.restTemplate = restTemplate;
        this.notificationUrl = notificationUrl;
    }

    public void notifyOrderPlaced(String email, Order order) {
        send(email, "Order #" + order.id() + " placed!",
                "Total: ₹" + order.pricing().total());
    }

    public void notifyStatusChange(String email, Order order) {
        send(email, "Order #" + order.id() + " update",
                "Status is now: " + order.status());
    }

    private void send(String email, String subject, String body) {
        try {
            restTemplate.postForObject(
                    notificationUrl + "/notifications/send",
                    Map.of("recipient", email, "channel", "EMAIL",
                            "subject", subject, "body", body),
                    Map.class
            );
        } catch (Exception e) {
            // notification failure must never break order flow
        }
    }
}
