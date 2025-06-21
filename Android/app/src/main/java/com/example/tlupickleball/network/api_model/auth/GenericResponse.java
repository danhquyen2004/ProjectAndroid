package com.example.tlupickleball.network.api_model.auth;

import com.google.gson.annotations.SerializedName;

public class GenericResponse {
    @SerializedName("message")
    private String message;

    public GenericResponse() {
        // Default constructor
    }
    public GenericResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
