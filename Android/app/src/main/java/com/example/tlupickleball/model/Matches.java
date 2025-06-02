package com.example.tlupickleball.model;
public class Matches {
    private String player1Name;
    private String player2Name;
    private int player1Avatar; // ID tài nguyên cho ảnh đại diện
    private int player2Avatar; // ID tài nguyên cho ảnh đại diện
    private String score;
    private String matchTime;
    private String matchStatus;

    public Matches(String player1Name, String player2Name, int player1Avatar, int player2Avatar, String score, String matchTime, String matchStatus) {
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        this.player1Avatar = player1Avatar;
        this.player2Avatar = player2Avatar;
        this.score = score;
        this.matchTime = matchTime;
        this.matchStatus = matchStatus;
    }

    // Getters
    public String getPlayer1Name() {
        return player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public int getPlayer1Avatar() {
        return player1Avatar;
    }

    public int getPlayer2Avatar() {
        return player2Avatar;
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

    // Setters (nếu cần, nếu không thì có thể bỏ qua cho lớp dữ liệu bất biến)
    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }

    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }

    public void setPlayer1Avatar(int player1Avatar) {
        this.player1Avatar = player1Avatar;
    }

    public void setPlayer2Avatar(int player2Avatar) {
        this.player2Avatar = player2Avatar;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void setMatchTime(String matchTime) {
        this.matchTime = matchTime;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }
}