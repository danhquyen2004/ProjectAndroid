package com.example.tlupickleball.model; // Đảm bảo đúng package của bạn

import com.google.gson.annotations.SerializedName;

public class Participant {
    @SerializedName("participantId")
    private String participantId;
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("avatarUrl") // THÊM DÒNG NÀY NẾU CHƯA CÓ
    private String avatarUrl;
    @SerializedName("team")
    private int team;
    // Thêm các trường khác nếu có

    // Constructor của bạn
    public Participant(String participantId, String fullName, String avatarUrl, int team) { // CẬP NHẬT CONSTRUCTOR
        this.participantId = participantId;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl; // GÁN GIÁ TRỊ
        this.team = team;
    }

    // Getters
    public String getParticipantId() {
        return participantId;
    }

    public String getFullName() {
        return fullName;
    }

    public int getTeam() {
        return team;
    }

    public String getAvatarUrl() { // THÊM PHƯƠNG THỨC NÀY
        return avatarUrl;
    }

    // Setters (tùy chọn, nếu cần)
    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setAvatarUrl(String avatarUrl) { // THÊM PHƯƠNG THỨC NÀY
        this.avatarUrl = avatarUrl;
    }

    public void setTeam(int team) {
        this.team = team;
    }
}