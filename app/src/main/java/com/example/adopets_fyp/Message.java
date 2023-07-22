package com.example.adopets_fyp;

public class Message {
    private String id;
    private String username;
    private String imageUrl;
    private String text;
    private String userId;

    public Message (){

    }

    public Message(String messageId, String userId, String username, String imageUrl, String messageText) {
        this.id=messageId;
        this.userId=userId;
        this.username=username;
        this.imageUrl=imageUrl;
        this.text=messageText;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Message(String id, String username, String imageUrl, String text) {
        this.id = id;
        this.username = username;
        this.imageUrl = imageUrl;
        this.text = text;
    }

    // Getters and setters
    // ...
}

