package com.example.tlupickleball.model;

public class Matches {
    // Các trường cũ có thể giữ lại hoặc điều chỉnh nếu cần
    private String league; // Giải đấu (có thể hiển thị ở đâu đó hoặc không)
    private String fullDateString; // Ví dụ: "14 tháng 5", "Thứ Sáu, 15 tháng 5" để hiển thị ở tvDateTitle

    // Các trường mới cho item_match_result.xml
    private String player1Name;
    private int player1AvatarResId; // Resource ID cho avatar (ví dụ: R.drawable.avatar_1)
    // Hoặc String nếu bạn load từ URL bằng Glide/Picasso
    private String player2Name;
    private int player2AvatarResId;
    private String score; // Ví dụ: "2 - 0"

    private boolean showDateTitle; // Để kiểm soát việc hiển thị tvDateTitle

    // Constructor (ví dụ)
    public Matches(String player1Name, int player1AvatarResId,
                      String player2Name, int player2AvatarResId,
                      String score, String fullDateString, String league, boolean showDateTitle) {
        this.player1Name = player1Name;
        this.player1AvatarResId = player1AvatarResId;
        this.player2Name = player2Name;
        this.player2AvatarResId = player2AvatarResId;
        this.score = score;
        this.fullDateString = fullDateString;
        this.league = league;
        this.showDateTitle = showDateTitle;
    }

    // Getters and Setters
    public String getPlayer1Name() {
        return player1Name;
    }

    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }

    public int getPlayer1AvatarResId() {
        return player1AvatarResId;
    }

    public void setPlayer1AvatarResId(int player1AvatarResId) {
        this.player1AvatarResId = player1AvatarResId;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }

    public int getPlayer2AvatarResId() {
        return player2AvatarResId;
    }

    public void setPlayer2AvatarResId(int player2AvatarResId) {
        this.player2AvatarResId = player2AvatarResId;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getFullDateString() {
        return fullDateString;
    }

    public void setFullDateString(String fullDateString) {
        this.fullDateString = fullDateString;
    }

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }

    public boolean isShowDateTitle() {
        return showDateTitle;
    }

    public void setShowDateTitle(boolean showDateTitle) {
        this.showDateTitle = showDateTitle;
    }

    // Giữ lại trường date để lọc nếu cần, hoặc điều chỉnh cho phù hợp
    private String dateForFilter; // Ví dụ "25/05" dùng để lọc từ DateAdapter

    public Matches(String player1Name, int player1AvatarResId,
                      String player2Name, int player2AvatarResId,
                      String score, String fullDateString, String league, boolean showDateTitle, String dateForFilter) {
        this.player1Name = player1Name;
        this.player1AvatarResId = player1AvatarResId;
        this.player2Name = player2Name;
        this.player2AvatarResId = player2AvatarResId;
        this.score = score;
        this.fullDateString = fullDateString;
        this.league = league;
        this.showDateTitle = showDateTitle;
        this.dateForFilter = dateForFilter;
    }
    public String getDateForFilter() { return dateForFilter; }
    public void setDateForFilter(String dateForFilter) { this.dateForFilter = dateForFilter; }
}