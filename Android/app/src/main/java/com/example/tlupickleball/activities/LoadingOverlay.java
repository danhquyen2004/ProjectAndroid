package com.example.tlupickleball.activities;

import android.app.Activity;
import android.view.View;

import com.example.tlupickleball.R;

public class LoadingOverlay {
    private final View overlayView;

    public LoadingOverlay(Activity activity) {
        overlayView = activity.findViewById(R.id.loadingOverlay);
    }

    public void show() {
        if (overlayView != null) {
            overlayView.setVisibility(View.VISIBLE);
        }
    }

    public void hide() {
        if (overlayView != null) {
            overlayView.setVisibility(View.GONE);
        }
    }
}
