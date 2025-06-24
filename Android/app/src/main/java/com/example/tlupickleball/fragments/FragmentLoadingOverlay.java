package com.example.tlupickleball.fragments;

import android.view.View;

import com.example.tlupickleball.R;

public class FragmentLoadingOverlay {
    private final View overlayView;

    public FragmentLoadingOverlay(View rootView) {
        overlayView = rootView.findViewById(R.id.loadingOverlay);
    }

    public void show() {
        if (overlayView != null) overlayView.setVisibility(View.VISIBLE);
    }

    public void hide() {
        if (overlayView != null) overlayView.setVisibility(View.GONE);
    }
}
