package com.desertUo.websockets;

import com.google.gson.annotations.SerializedName;

public class WSChatMessage {
    @SerializedName("action")
    private String action; // bc: Broadcast, pm: Private Message, welcome: To get to know our ID
    @SerializedName("sender_id")
    private String senderId;
    @SerializedName("target_id")
    private String targetId;
    @SerializedName("message")
    private String message;

    public WSChatMessage(String message) {
        this.action = "bc";
        this.senderId = "";
        this.targetId = "";
        this.message = message;
    }

    public WSChatMessage(String targetId, String message) {
        this.action = "pm";
        this.senderId = "";
        this.targetId = targetId;
        this.message = message;
    }

    public String getAction() { return this.action; }
    public String getSenderId() { return this.senderId; }
    public String getTargetId() { return this.targetId; }
    public String getMessage() { return this.message; }

    public void setSenderId(String senderId) { this.senderId = senderId; }
}
