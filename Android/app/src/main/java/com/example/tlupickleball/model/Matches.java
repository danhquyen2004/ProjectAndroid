// File: com.example.tlupickleball.model.Matches.java
package com.example.tlupickleball.model;

public class Matches {

    private String matchId;
    private String player1Name;
    private String player2Name;
    private String player1AvatarUrl1;
    private String player1AvatarUrl2;
    private String player2AvatarUrl1;
    private String player2AvatarUrl2;
    private String score;
    private String matchTime;
    private String matchStatus;
    private boolean isDoublesMatch;

    public Matches(String matchId, String player1Name, String player2Name, String player1AvatarUrl1, String player1AvatarUrl2, String player2AvatarUrl1, String player2AvatarUrl2, String score, String matchTime, String matchStatus, boolean isDoublesMatch) {
        this.matchId = matchId;
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        this.player1AvatarUrl1 = player1AvatarUrl1;
        this.player1AvatarUrl2 = player1AvatarUrl2;
        this.player2AvatarUrl1 = player2AvatarUrl1;
        this.player2AvatarUrl2 = player2AvatarUrl2;
        this.score = score;
        this.matchTime = matchTime;
        this.matchStatus = matchStatus;
        this.isDoublesMatch = isDoublesMatch;
    }

    public String getMatchId() {
        return matchId;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public String getPlayer1AvatarUrl1() {
        return player1AvatarUrl1;
    }

    public String getPlayer1AvatarUrl2() {
        return player1AvatarUrl2;
    }

    public String getPlayer2AvatarUrl1() {
        return player2AvatarUrl1;
    }

    public String getPlayer2AvatarUrl2() {
        return player2AvatarUrl2;
    }

    public String getScore() {
        return score;
    }

    public String getMatchTime() {
        return matchTime;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public boolean isDoublesMatch() {
        return isDoublesMatch;
    }
}