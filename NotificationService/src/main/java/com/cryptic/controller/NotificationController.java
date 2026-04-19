package com.cryptic.controller;

import com.cryptic.model.NotificationLog;
import com.cryptic.model.NotificationRequest;
import com.cryptic.repo.NotificationLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);
    private final NotificationLogRepository logRepository;

    public NotificationController(NotificationLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> send(
            @RequestBody NotificationRequest request) {

        log.info("[{}] To: {} | Subject: {}",
                request.channel(), request.recipient(), request.subject());

        NotificationLog saved = logRepository.save(new NotificationLog(
                request.recipient(), request.channel(),
                request.subject(), request.body(), "SENT"
        ));

        return ResponseEntity.ok(Map.of(
                "status", "SENT",
                "logId", saved.getId(),
                "channel", request.channel(),
                "recipient", request.recipient()
        ));
    }

    @GetMapping("/logs/{recipient}")
    public ResponseEntity<List<NotificationLog>> getLogs(
            @PathVariable String recipient) {
        return ResponseEntity.ok(logRepository.findByRecipient(recipient));
    }
}
