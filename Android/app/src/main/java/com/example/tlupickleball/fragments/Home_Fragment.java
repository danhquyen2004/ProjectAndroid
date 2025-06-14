package com.example.tlupickleball.fragments;

import android.os.Bundle;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.example.tlupickleball.R;
import com.example.tlupickleball.activities.MainActivity;
import com.example.tlupickleball.activities.UserActivity;
import com.google.android.material.navigation.NavigationView;


public class Home_Fragment extends Fragment {
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Ví dụ: Button mở drawer
        View btnOpenDrawer = rootView.findViewById(R.id.btnMenu);
        btnOpenDrawer.setOnClickListener(v -> {
            // Truy cập Drawer từ MainActivity
            DrawerLayout drawerLayout = ((UserActivity) requireActivity()).drawerLayout;
            if (drawerLayout != null) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        return rootView;
    }
}
