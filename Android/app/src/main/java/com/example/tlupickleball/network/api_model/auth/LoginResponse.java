package com.example.tlupickleball.network.api_model.auth;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("idToken")
    private String idToken;
    @SerializedName("refreshToken")
    private String refreshToken;
    @SerializedName("uid")
    private String uid;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @SerializedName("role")
    private String role;
    @SerializedName("emailVerified")
    private boolean emailVerified;
    @SerializedName("disabled")
    private boolean disabled;

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    public LoginResponse() {
        // Default constructor
    }
    public LoginResponse(String idToken, String refreshToken, String uid, String role, boolean emailVerified, boolean disabled) {
        this.idToken = idToken;
        this.refreshToken = refreshToken;
        this.uid = uid;
        this.role = role;
        this.emailVerified = emailVerified;
        this.disabled = disabled;
    }
}
