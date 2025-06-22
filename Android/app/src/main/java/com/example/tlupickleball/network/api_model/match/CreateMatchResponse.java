package com.example.tlupickleball.network.api_model.match;

import com.google.gson.annotations.SerializedName;

public class CreateMatchResponse {

    @SerializedName("message")
    private String message; // Ví dụ: "Match created successfully!"
    @SerializedName("matchId")
    private String matchId; // ID của trận đấu vừa được tạo

    // Constructor (tùy chọn, nếu bạn cần khởi tạo trong ứng dụng)
    public CreateMatchResponse(String message, String matchId) {
        this.message = message;
        this.matchId = matchId;
    }

    // Getters
    public String getMessage() {
        return message;
    }

    public String getMatchId() {
        return matchId;
    }

    // Setters (tùy chọn, thường không cần nếu chỉ dùng để nhận phản hồi từ API)
    public void setMessage(String message) {
        this.message = message;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    @Override
    public String toString() {
        return "CreateMatchResponse{" +
                "message='" + message + '\'' +
                ", matchId='" + matchId + '\'' +
                '}';
    }
}