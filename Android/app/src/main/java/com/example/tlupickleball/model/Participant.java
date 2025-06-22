package com.example.tlupickleball.model;

import com.google.gson.annotations.SerializedName;

public class Participant {
    @SerializedName("userId")
    private String userId;
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("team")
    private int team;
    @SerializedName("isConfirmed")
    private boolean isConfirmed;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }
}