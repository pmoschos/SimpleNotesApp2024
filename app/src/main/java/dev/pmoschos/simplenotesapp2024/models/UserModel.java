package dev.pmoschos.simplenotesapp2024.models;

public class UserModel {
    private String userName;
    private String email;
    private String userId;
    private String profileImage;

    public UserModel() {}

    public UserModel(String userName, String email, String userId, String profileImage) {
        this.userName = userName;
        this.email = email;
        this.userId = userId;
        this.profileImage = profileImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", userId='" + userId + '\'' +
                ", profileImage='" + profileImage + '\'' +
                '}';
    }
}
