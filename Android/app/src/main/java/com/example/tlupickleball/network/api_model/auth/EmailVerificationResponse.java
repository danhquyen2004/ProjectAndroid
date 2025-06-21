package com.example.tlupickleball.network.api_model.auth;

import com.google.gson.annotations.SerializedName;

public class EmailVerificationResponse {
    @SerializedName("emailVerified")
    private boolean emailVerified;

    public EmailVerificationResponse() {
        // Default constructor
    }

    public EmailVerificationResponse(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
}
