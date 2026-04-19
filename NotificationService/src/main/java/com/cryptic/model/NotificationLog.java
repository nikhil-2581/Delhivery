package com.cryptic.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "notification_logs")
public class NotificationLog {

    @Id
    private String id;

    private String recipient;
    private String channel;
    private String subject;
    private String body;
    private String status;
    private Instant sentAt;

    public NotificationLog(String recipient, String channel,
                           String subject, String body, String status) {
        this.recipient = recipient;
        this.channel = channel;
        this.subject = subject;
        this.body = body;
        this.status = status;
        this.sentAt = Instant.now();
    }

    public String getId()        { return id; }
    public String getRecipient() { return recipient; }
    public String getChannel()   { return channel; }
    public String getSubject()   { return subject; }
    public String getBody()      { return body; }
    public String getStatus()    { return status; }
    public Instant getSentAt()   { return sentAt; }
}
