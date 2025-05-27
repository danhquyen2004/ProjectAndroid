package com.example.tlupickleball.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tlupickleball.fragments.Club_Finance_Fragment;
import com.example.tlupickleball.fragments.Personal_Finance_Fragment;

public class Transaction_adapter_fragment extends FragmentStateAdapter {

    public Transaction_adapter_fragment(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1:
                return new Club_Finance_Fragment();
        }
        return new Personal_Finance_Fragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
