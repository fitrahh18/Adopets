package com.example.adopets_fyp;

public class Post {
    private String postId;
    private String imageUrl;
    private String petName;

    private String userId;

    private String formattedDatePost;
    private String petAge;
    private String petStatus;

    private String petBreed;
    private double longitude,latitude;


    public Post(String postId, String imageUrl, String petName, String petAge, String petBreed, String userId, String petGender, String petSpecies, String petStatus, String formattedDatePost, double latitude, double longitude) {
        this.postId = postId;
        this.imageUrl = imageUrl;
        this.petName = petName;
        this.petAge = petAge;
        this.petGender = petGender;
        this.petSpecies = petSpecies;
        this.petBreed = petBreed;
        this.petStatus = petStatus;
        this.userId = userId;
        this.formattedDatePost=formattedDatePost;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Post() {

    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getPostId() {
        return postId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public String getPetBreed() {
        return petBreed;
    }

    public String getPetStatus() {
        return petStatus;
    }
    public String getPetName() {
        return petName;
    }

    public String getFormattedDatePost() {
        return formattedDatePost;
    }

    public String getPetAge() {
        return petAge;
    }

    public String getPetGender() {
        return petGender;
    }

    public String getPetSpecies() {
        return petSpecies;
    }

    private String petGender;
    private String petSpecies;

}

