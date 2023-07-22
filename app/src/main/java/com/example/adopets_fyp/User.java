package com.example.adopets_fyp;

public class User {
    private String userimageUrl;
    private String username;
    private String useremail;

    public User(String userimageUrl) {
        this.userimageUrl = userimageUrl;

    }
    public User() {

    }

    public String getUserimageUrl() {
        return userimageUrl;
    }

    public void setUserimageUrl(String userimageUrl) {
        this.userimageUrl = userimageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }
}
