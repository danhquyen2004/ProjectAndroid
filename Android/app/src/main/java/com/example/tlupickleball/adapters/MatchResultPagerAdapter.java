package com.example.tlupickleball.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tlupickleball.fragments.GeneralResultFragment;
import com.example.tlupickleball.fragments.PersonalResultFragment;

public class MatchResultPagerAdapter extends FragmentStateAdapter {
    public MatchResultPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return (position == 0) ? new PersonalResultFragment() : new GeneralResultFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

