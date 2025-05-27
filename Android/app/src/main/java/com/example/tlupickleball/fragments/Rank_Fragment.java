package com.example.tlupickleball.fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tlupickleball.R;
import com.example.tlupickleball.adapters.RankAdapter;


public class Rank_Fragment extends Fragment {
    private ViewPager2 viewPager;
    private ColorStateList def;
    private TextView item1, item2, select;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_rank_with_tab_layout, container, false);

        initViews();
        setupViewPager();
        setupListeners();

        return rootView;
    }

    private void initViews() {
        item1 = rootView.findViewById(R.id.item1);
        item1.setText("Đấu đơn");
        item2 = rootView.findViewById(R.id.item2);
        item2.setText("Đấu đôi");
        select = rootView.findViewById(R.id.select);
        def = item2.getTextColors();
        viewPager = rootView.findViewById(R.id.viewPager);
    }

    private void setupViewPager() {
        FragmentManager manager = getChildFragmentManager();
        RankAdapter rankAdapter = new RankAdapter(manager, getLifecycle());
        viewPager.setAdapter(rankAdapter);
    }

    private void setupListeners() {
        item1.setOnClickListener(v -> {
            select.animate().x(0).setDuration(100);
            item1.setTextColor(Color.WHITE);
            item2.setTextColor(def);
            viewPager.setCurrentItem(0);
        });

        item2.setOnClickListener(v -> {
            item1.setTextColor(def);
            item2.setTextColor(Color.WHITE);
            int size = item2.getWidth();
            select.animate().x(size).setDuration(100);
            viewPager.setCurrentItem(1);
        });
    }
}