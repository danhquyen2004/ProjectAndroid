package com.example.tlupickleball.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tlupickleball.fragments.Duo_Rank_Fragment;
import com.example.tlupickleball.fragments.Solo_Rank_Fragment;

public class RankAdapter extends FragmentStateAdapter {
    public RankAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1:
                return new Duo_Rank_Fragment();
        }
        return new Solo_Rank_Fragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
