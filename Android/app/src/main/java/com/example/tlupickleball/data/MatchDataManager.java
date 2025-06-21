package com.example.tlupickleball.data;

import com.example.tlupickleball.model.Matches; // Đảm bảo import đúng lớp Matches

import java.util.List;

public interface MatchDataManager {

    interface MatchDataCallback {
        void onSuccess(List<Matches> matches);
        void onError(String errorMessage);
    }

    interface SubmitMatchCallback {
        void onSuccess(String message);
        void onError(String errorMessage);
    }

    void getAvailableMatches(MatchDataCallback callback);
    void submitNewMatch(Matches match, SubmitMatchCallback callback);
    // Bạn có thể thêm các phương thức khác ở đây như getPlayers, updateMatch, v.v.
}