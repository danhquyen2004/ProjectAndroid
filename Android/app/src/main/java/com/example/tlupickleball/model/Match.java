package com.example.tlupickleball.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Match {
    @SerializedName("matchId")
    private String matchId;
    @SerializedName("status")
    private String status;
    @SerializedName("startTime")
    private String startTime;
    @SerializedName("participants")
    private List<Participant> participants;
    @SerializedName("team1Wins")
    private int team1Wins;
    @SerializedName("team2Wins")
    private int team2Wins;

    public Match(String matchId, String status, String startTime, List<Participant> participants, int team1Wins, int team2Wins) {
        this.matchId = matchId;
        this.status = status;
        this.startTime = startTime;
        this.participants = participants;
        this.team1Wins = team1Wins;
        this.team2Wins = team2Wins;
    }

    // Getters
    public String getMatchId() {
        return matchId;
    }

    public String getStatus() {
        return status;
    }

    public String getStartTime() {
        return startTime;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public int getTeam1Wins() {
        return team1Wins;
    }

    public int getTeam2Wins() {
        return team2Wins;
    }
    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public void setTeam1Wins(int team1Wins) {
        this.team1Wins = team1Wins;
    }

    public void setTeam2Wins(int team2Wins) {
        this.team2Wins = team2Wins;
    }
}