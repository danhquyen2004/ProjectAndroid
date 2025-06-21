package com.example.tlupickleball.model;

public class User {
    private String uid;
    private String name;
    private String dob;
    private String gender;

    public boolean isApproved() {
        return approved;
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

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    private String email;
    private String role;
    private boolean approved;
    private String createdAt; // có thể dùng Timestamp nếu parse từ Firestore

    // Constructors
    public User() {}

    public User(String uid, String name, String dob, String gender, String email,
                String role, boolean approved, String createdAt) {
        this.uid = uid;
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.email = email;
        this.role = role;
        this.approved = approved;
        this.createdAt = createdAt;
    }
}
