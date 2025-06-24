package com.example.tlupickleball.network.api_model.auth;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {
    @SerializedName("message")
    private String message;
    @SerializedName("idToken")
    private String idToken;
    @SerializedName("role")
    private String role;
    @SerializedName("refreshToken")
    private String refreshToken;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    @SerializedName("uid")
    private String uid;

    public RegisterResponse()
    {

    }

    public RegisterResponse(String message, String uid, String idToken, String role,String refreshToken) {
        this.message = message;
        this.uid = uid;
        this.idToken = idToken;
        this.role = role;
        this.refreshToken = refreshToken;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
