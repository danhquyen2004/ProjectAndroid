package com.example.tlupickleball.model;

public class MatchResult {
    private String player1;
    private String player2;
    private String score;

    public MatchResult(String player1, String player2, String score) {
        this.player1 = player1;
        this.player2 = player2;
        this.score = score;
    }

    public String getPlayer1() {
        return player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public String getScore() {
        return score;
    }
}
