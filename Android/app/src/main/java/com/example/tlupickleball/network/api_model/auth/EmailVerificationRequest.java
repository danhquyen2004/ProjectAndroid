package com.example.tlupickleball.network.api_model.auth;

public class EmailVerificationRequest {
    private String idToken;
    public EmailVerificationRequest() {
        // Default constructor
    }

    public EmailVerificationRequest(String idToken) {
        this.idToken = idToken;
    }

    public String getIdToken() {
        return idToken;
    }
    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
}
