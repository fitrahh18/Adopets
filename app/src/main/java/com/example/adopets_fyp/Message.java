package com.example.adopets_fyp;

public class Message {
    private String messageId;
    private String senderUid;
    private String receiverUid;
    private String senderUsername;
    private String messageText;
    private long timestamp;

    public Message() {
        // Required empty constructor for Firebase
    }

    public Message(String messageId, String senderUid, String receiverUid, String senderUsername, String messageText, long timestamp) {
        this.messageId = messageId;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.senderUsername = senderUsername;
        this.messageText = messageText;
        this.timestamp = timestamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public String getMessageText() {
        return messageText;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
