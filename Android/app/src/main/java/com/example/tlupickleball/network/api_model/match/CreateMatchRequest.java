// File: com.example.tlupickleball.network.api_model.match.CreateMatchRequest.java

package com.example.tlupickleball.network.api_model.match;

import com.google.gson.annotations.SerializedName;

import java.util.List;



public class CreateMatchRequest {
    @SerializedName("startDate")
    private String startDate;
    @SerializedName("startTime")
    private String startTime;
    @SerializedName("type")
    private String type; // e.g., "single" or "double"
    @SerializedName("setCount")
    private int setCount;
    @SerializedName("teams")
    private Teams teams;
    @SerializedName("setResults")
    private List<SetResult> setResults; // Can be empty for initial creation

    public CreateMatchRequest(String startDate, String startTime, String type, int setCount, Teams teams, List<SetResult> setResults) {
        this.startDate = startDate;
        this.startTime = startTime;
        this.type = type;
        this.setCount = setCount;
        this.teams = teams;
        this.setResults = setResults;
    }

    // Getters
    public String getStartDate() { return startDate; }
    public String getStartTime() { return startTime; }
    public String getType() { return type; }
    public int getSetCount() { return setCount; }
    public Teams getTeams() { return teams; }
    public List<SetResult> getSetResults() { return setResults; }

    // Nested class for Teams
    public static class Teams {
        @SerializedName("team1")
        private List<String> team1; // List of player UUIDs
        @SerializedName("team2")
        private List<String> team2;

        public Teams(List<String> team1, List<String> team2) {
            this.team1 = team1;
            this.team2 = team2;
        }

        // Getters
        public List<String> getTeam1() { return team1; }
        public List<String> getTeam2() { return team2; }
    }

    public static class SetResult {
        @SerializedName("setNumber")
        private int setNumber;
        @SerializedName("team1Score")
        private int team1Score;
        @SerializedName("team2Score")
        private int team2Score;

        public SetResult(int setNumber, int team1Score, int team2Score) {
            this.setNumber = setNumber;
            this.team1Score = team1Score;
            this.team2Score = team2Score;
        }

        // Getters
        public int getSetNumber() { return setNumber; }
        public int getTeam1Score() { return team1Score; }
        public int getTeam2Score() { return team2Score; }
    }
}