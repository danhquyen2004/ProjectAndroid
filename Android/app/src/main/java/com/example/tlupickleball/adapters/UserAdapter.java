package com.example.tlupickleball.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tlupickleball.fragments.Duo_Rank_Fragment;
import com.example.tlupickleball.fragments.Finance_Fragment;
import com.example.tlupickleball.fragments.Home_Fragment;
import com.example.tlupickleball.fragments.Matches_Fragment;
import com.example.tlupickleball.fragments.Rank_Fragment;

public class UserAdapter extends FragmentStateAdapter {
    public UserAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1:
                return new Matches_Fragment();
            case 2:
                return new Finance_Fragment();
            case 3:
                return new Rank_Fragment();
        }
        return new Home_Fragment();
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
