package com.example.tlupickleball.network.api_model.match;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CreateMatchRequest {
    @SerializedName("startDate")
    private String startDate;
    @SerializedName("startTime")
    private String startTime;
    @SerializedName("type")
    private String type;
    @SerializedName("setCount")
    private int setCount;
    @SerializedName("teams")
    private Teams teams;
    @SerializedName("setResults")
    private List<SetResult> setResults;

    public CreateMatchRequest(String startDate, String startTime, String type, int setCount, Teams teams, List<SetResult> setResults) {
        this.startDate = startDate;
        this.startTime = startTime;
        this.type = type;
        this.setCount = setCount;
        this.teams = teams;
        this.setResults = setResults;
    }

    public static class Teams {
        @SerializedName("team1")
        private List<String> team1;
        @SerializedName("team2")
        private List<String> team2;

        public Teams(List<String> team1, List<String> team2) {
            this.team1 = team1;
            this.team2 = team2;
        }
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
    }
}