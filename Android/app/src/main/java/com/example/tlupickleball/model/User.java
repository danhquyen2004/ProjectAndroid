package com.example.tlupickleball.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("uid")
    private String uid;
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("birthDate")
    private String birthDate;
    @SerializedName("gender")
    private String gender;
    @SerializedName("email")
    private String email;
    @SerializedName("role")
    private String role;
    @SerializedName("approved")
    private boolean approved;
    @SerializedName("avatarUrl")
    private String avatarUrl; // Thêm trường ảnh đại diện nếu cần
    @SerializedName("currentSingleScore")
    private float currentSingleScore;
    @SerializedName("currentDoubleScore")
    private float currentDoubleScore;
    @SerializedName("createdAt")
    private String createdAt; // có thể dùng Timestamp nếu parse từ Firestore

    public float getCurrentSingleScore() {
        return currentSingleScore;
    }

    public void setCurrentSingleScore(int currentSingleScore) {
        this.currentSingleScore = currentSingleScore;
    }

    public float getCurrentDoubleScore() {
        return currentDoubleScore;
    }

    public void setCurrentDoubleScore(int currentDoubleScore) {
        this.currentDoubleScore = currentDoubleScore;
    }

    public boolean isApproved() {
        return approved;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    // Constructors
    public User() {}

    public User(String uid, String fullName, String birthDate, String gender, String email,
                String role, boolean approved, String avatarUrl, float currentSingleScore, float currentDoubleScore, String createdAt) {
        this.uid = uid;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.email = email;
        this.role = role;
        this.approved = approved;
        this.avatarUrl = avatarUrl;
        this.currentSingleScore = currentSingleScore;
        this.currentDoubleScore = currentDoubleScore;
        this.createdAt = createdAt;
    }
}
