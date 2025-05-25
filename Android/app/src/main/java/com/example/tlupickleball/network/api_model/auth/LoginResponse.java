package com.example.tlupickleball.network.api_model.auth;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("idToken")
    private String idToken;
    @SerializedName("refreshToken")
    private String refreshToken;
    @SerializedName("uid")
    private String uid;

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
    public LoginResponse(String idToken, String refreshToken, String uid) {
        this.idToken = idToken;
        this.refreshToken = refreshToken;
        this.uid = uid;
    }
}
