package com.example.tlupickleball.activities.base;

import androidx.fragment.app.Fragment;

import com.example.tlupickleball.activities.LoadingOverlay;

public class BaseFragment extends Fragment {
    private LoadingOverlay loadingOverlay;

    @Override
    public void onStart() {
        super.onStart();
        loadingOverlay = new LoadingOverlay(requireActivity());
    }

    public void showLoading() {
        if (loadingOverlay != null) loadingOverlay.show();
        else {
            loadingOverlay = new LoadingOverlay(requireActivity());
            loadingOverlay.show();
        }
    }

    public void hideLoading() {
        if (loadingOverlay != null) loadingOverlay.hide();
        else {
            loadingOverlay = new LoadingOverlay(requireActivity());
            loadingOverlay.hide();
        }
    }
}
