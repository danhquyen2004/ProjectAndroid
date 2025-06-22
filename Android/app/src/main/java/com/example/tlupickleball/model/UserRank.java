package com.example.tlupickleball.model;

import com.google.gson.annotations.SerializedName;

public class UserRank {
    @SerializedName("rank")
    private String rank;
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("avatarUrl")
    private String avatarUrl;
    @SerializedName("point")
    private float point;

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public float getPoint() {
        return point;
    }

    public void setPoint(float point) {
        this.point = point;
    }

    public UserRank(String rank, String fullName, float point) {
        this.rank = rank;
        this.fullName = fullName;
        this.point = point;
    }
}
