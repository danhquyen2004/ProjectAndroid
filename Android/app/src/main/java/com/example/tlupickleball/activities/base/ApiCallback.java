package com.example.tlupickleball.activities.base;

public interface ApiCallback {
    void onSuccess();
    void onFailure(String errorMessage);
    void onComplete();
}
