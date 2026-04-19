package com.cryptic.repo;

import com.cryptic.model.NotificationLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationLogRepository
        extends MongoRepository<NotificationLog, String> {
    List<NotificationLog> findByRecipient(String recipient);
    List<NotificationLog> findByChannel(String channel);
}
