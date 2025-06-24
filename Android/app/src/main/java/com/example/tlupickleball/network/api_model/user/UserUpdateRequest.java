package com.example.tlupickleball.network.api_model.user;

import com.google.gson.annotations.SerializedName;

public class UserUpdateRequest {
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("birthdate")
    private String birthdate;
    @SerializedName("gender")
    private String gender;
    @SerializedName("role")
    private String role;
    @SerializedName("approved")
    private boolean approved;

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
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

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


}
