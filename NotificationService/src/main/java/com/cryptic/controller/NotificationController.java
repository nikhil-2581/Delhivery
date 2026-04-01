package com.cryptic.controller;

import com.cryptic.model.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> send(
            @RequestBody NotificationRequest request) {
        log.info("[{}] To: {} | Subject: {} | Body: {}",
                request.channel(), request.recipient(),
                request.subject(), request.body());
        return ResponseEntity.ok(Map.of(
                "status", "SENT",
                "channel", request.channel(),
                "recipient", request.recipient()
        ));
    }
}
