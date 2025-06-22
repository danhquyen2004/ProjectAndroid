package com.example.tlupickleball.model;

public class Matches {
    private String player1Name;
    private String player2Name;
    private String player1AvatarUrl1; // URL avatar người chơi 1 đội 1
    private String player1AvatarUrl2; // URL avatar người chơi 2 đội 1 (nếu đấu đôi)
    private String player2AvatarUrl1; // URL avatar người chơi 1 đội 2
    private String player2AvatarUrl2; // URL avatar người chơi 2 đội 2 (nếu đấu đôi)
    private String score;
    private String matchTime;
    private String matchStatus;
    private boolean isDoublesMatch; // Thêm trường này để dễ kiểm tra loại trận đấu

    public Matches(String player1Name, String player2Name,
                   String player1AvatarUrl1, String player1AvatarUrl2,
                   String player2AvatarUrl1, String player2AvatarUrl2,
                   String score, String matchTime, String matchStatus, boolean isDoublesMatch) {
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

    // Getters
    public String getPlayer1Name() { return player1Name; }
    public String getPlayer2Name() { return player2Name; }
    public String getPlayer1AvatarUrl1() { return player1AvatarUrl1; }
    public String getPlayer1AvatarUrl2() { return player1AvatarUrl2; }
    public String getPlayer2AvatarUrl1() { return player2AvatarUrl1; }
    public String getPlayer2AvatarUrl2() { return player2AvatarUrl2; }
    public String getScore() { return score; }
    public String getMatchTime() { return matchTime; }
    public String getMatchStatus() { return matchStatus; }
    public boolean isDoublesMatch() { return isDoublesMatch; }

    // Setters (tùy chọn)
    public void setPlayer1Name(String player1Name) { this.player1Name = player1Name; }
    public void setPlayer2Name(String player2Name) { this.player2Name = player2Name; }
    public void setPlayer1AvatarUrl1(String player1AvatarUrl1) { this.player1AvatarUrl1 = player1AvatarUrl1; }
    public void setPlayer1AvatarUrl2(String player1AvatarUrl2) { this.player1AvatarUrl2 = player1AvatarUrl2; }
    public void setPlayer2AvatarUrl1(String player2AvatarUrl1) { this.player2AvatarUrl1 = player2AvatarUrl1; }
    public void setPlayer2AvatarUrl2(String player2AvatarUrl2) { this.player2AvatarUrl2 = player2AvatarUrl2; }
    public void setScore(String score) { this.score = score; }
    public void setMatchTime(String matchTime) { this.matchTime = matchTime; }
    public void setMatchStatus(String matchStatus) { this.matchStatus = matchStatus; }
    public void setDoublesMatch(boolean doublesMatch) { isDoublesMatch = doublesMatch; }
}