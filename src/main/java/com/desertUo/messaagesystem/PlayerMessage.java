package com.desertUo.messaagesystem;

import org.apache.commons.lang3.StringEscapeUtils;
import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.util.UUID;

public class PlayerMessage {
    @BsonId
    private ObjectId id;
    private UUID sender;
    private UUID recipient;
    private String content;
    private MessageStatus status;
    private long timestamp;

    public enum MessageStatus {UNREAD, READ, ARCHIVED}

    public PlayerMessage() {
    }

    public PlayerMessage(UUID sender, UUID recipient, String content) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
        this.status = MessageStatus.UNREAD;
    }

    public ObjectId getId() {
        return id;
    }

    public UUID getSender() {
        return sender;
    }

    public void setSender(UUID sender) {
        this.sender = sender;
    }

    public UUID getRecipient() {
        return recipient;
    }

    public void setRecipient(UUID recipient) {
        this.recipient = recipient;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Document toDocument() {
        String safeMessage = StringEscapeUtils.escapeHtml4(this.content);

        return new Document("sender", this.sender.toString())
                .append("receiver", this.recipient.toString())
                .append("message", safeMessage)
                .append("timestamp", this.timestamp)
                .append("status", this.status.name()); // Stores as "UNREAD", "READ", etc.
    }

    public static PlayerMessage fromDocument(Document doc) {
        PlayerMessage msg = new PlayerMessage(
                UUID.fromString(doc.getString("sender")),
                UUID.fromString(doc.getString("receiver")),
                doc.getString("message")
        );
        msg.setStatus(MessageStatus.valueOf(doc.getString("status")));
        return msg;
    }
}
