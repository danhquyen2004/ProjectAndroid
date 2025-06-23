package com.example.tlupickleball.network.api_model.match;

import com.google.gson.annotations.SerializedName;

public class CreateMatchResponse {

    @SerializedName("message")
    private String message;
    @SerializedName("matchId")
    private String matchId;

    public String getMessage() {
        return message;
    }
    public String getMatchId() {
        return matchId;
    }
}