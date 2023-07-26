package com.example.adopets_fyp;

public class User {
    private String userimageUrl;
    private String username;
    private String useremail;

    private String phone;

    public User() {
        //for firebase
    }

    public User(String phone, String useremail, String userimageUrl, String username) {
        this.phone = phone;
        this.useremail = useremail;
        this.userimageUrl = userimageUrl;
        this.username = username;
    }

    public User(String userimageUrl) {
        this.userimageUrl = userimageUrl;

    }

    public String getPhone() {
        return phone;
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
