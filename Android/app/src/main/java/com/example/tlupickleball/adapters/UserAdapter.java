package com.example.tlupickleball.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tlupickleball.fragments.Duo_Rank_Fragment;
import com.example.tlupickleball.fragments.Finance_Fragment;
import com.example.tlupickleball.fragments.Home_Fragment;
import com.example.tlupickleball.fragments.Matches_Fragment;
import com.example.tlupickleball.fragments.Member_Controller;
import com.example.tlupickleball.fragments.Rank_Fragment;
import com.example.tlupickleball.network.core.SessionManager;

public class UserAdapter extends FragmentStateAdapter {
    private final String userRole;
    public UserAdapter(Context context, @NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        this.userRole = SessionManager.getRole(context);
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
            case 4:
                return new Member_Controller();
        }
        return new Home_Fragment();
    }

    @Override
    public int getItemCount() {
        // Trả về số lượng tab dựa trên vai trò
        if ("admin".equals(userRole)) {
            return 5; // Admin có 5 tab
        } else {
            return 4; // Người dùng thường có 4 tab
        }
    }
}
