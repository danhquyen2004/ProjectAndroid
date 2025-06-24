package com.example.tlupickleball.model;

import com.google.gson.annotations.SerializedName; // Import thêm

public class MatchSet {
    // Thêm trường setNumber
    @SerializedName("setNumber")
    private int setNumber;

    @SerializedName("team1Score")
    private int team1Score;

    @SerializedName("team2Score")
    private int team2Score;

    // Constructor có thể bỏ trống hoặc cập nhật nếu cần
    public MatchSet(int setNumber, int team1Score, int team2Score) {
        this.setNumber = setNumber;
        this.team1Score = team1Score;
        this.team2Score = team2Score;
    }

    // Getters
    public int getSetNumber() { // Thêm getter
        return setNumber;
    }

    public int getTeam1Score() {
        return team1Score;
    }

    public int getTeam2Score() {
        return team2Score;
    }

    // Setters
    public void setSetNumber(int setNumber) { // Thêm setter
        this.setNumber = setNumber;
    }

    public void setTeam1Score(int team1Score) {
        this.team1Score = team1Score;
    }

    public void setTeam2Score(int team2Score) {
        this.team2Score = team2Score;
    }
}