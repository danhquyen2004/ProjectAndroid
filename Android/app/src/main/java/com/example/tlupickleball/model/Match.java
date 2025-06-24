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
    // Thêm các trường khác nếu có, ví dụ: type, setCount, setResults từ API
    @SerializedName("type")
    private String type; // e.g., "single" or "double"
    @SerializedName("setCount")
    private int setCount;
    @SerializedName("setResults")
    private List<MatchSet> setResults; // Dùng MatchSet đã tạo

    public Match(String matchId, String status, String startTime, List<Participant> participants, int team1Wins, int team2Wins, String type, int setCount, List<MatchSet> setResults) {
        this.matchId = matchId;
        this.status = status;
        this.startTime = startTime;
        this.participants = participants;
        this.team1Wins = team1Wins;
        this.team2Wins = team2Wins;
        this.type = type;
        this.setCount = setCount;
        this.setResults = setResults;
    }

    public Match() {
        // Constructor mặc định cho Gson
    }

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
    public String getType() { return type; }
    public int getSetCount() { return setCount; }
    public List<MatchSet> getSetResults() { return setResults; }


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
    public void setType(String type) { this.type = type; }
    public void setSetCount(int setCount) { this.setCount = setCount; }
    public void setSetResults(List<MatchSet> setResults) { this.setResults = setResults; }
}