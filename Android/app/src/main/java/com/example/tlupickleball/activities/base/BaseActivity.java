package com.example.tlupickleball.activities.base;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tlupickleball.activities.LoadingOverlay;

public abstract class BaseActivity extends AppCompatActivity {
    private LoadingOverlay loadingOverlay;

    @Override
    protected void onStart() {
        super.onStart();
        loadingOverlay = new LoadingOverlay(this);
    }

    public void showLoading() {
        if (loadingOverlay != null) loadingOverlay.show();
    }

    public void hideLoading() {
        if (loadingOverlay != null) loadingOverlay.hide();
    }
}
