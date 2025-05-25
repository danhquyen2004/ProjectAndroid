package com.example.tlupickleball.fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tlupickleball.R;
import com.example.tlupickleball.activities.Rank;
import com.example.tlupickleball.adapters.PlayerAdapter;
import com.example.tlupickleball.adapters.RankAdapter;
import com.example.tlupickleball.model.Player;

import java.util.ArrayList;
import java.util.List;


public class Solo_Rank_Fragment extends Fragment {

    private RecyclerView recyclerView;
    private PlayerAdapter adapter;
    private List<Player> topPlayer;
    private View rootView;
    private Rank.TopThreeViewHolder topThreeViewHolder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_solo_rank, container, false);

        initData();
        setupTopThree();
        setupRecyclerView();

        return rootView;
    }

    private void initData() {
        topPlayer = new ArrayList<>();
        topPlayer.add(new Player("Ahri", 2.1, R.drawable.avatar_1));
        topPlayer.add(new Player("Lux", 1.9, R.drawable.avatar_1));
        topPlayer.add(new Player("Jinx", 1.8, R.drawable.avatar_1));
        topPlayer.add(new Player("Vi", 1.6, R.drawable.avatar_1));
        topPlayer.add(new Player("Zed", 1.5, R.drawable.avatar_1));
    }

    private void setupTopThree() {
        View top3Layout = rootView.findViewById(R.id.top3Layout);
        topThreeViewHolder = new Rank.TopThreeViewHolder(top3Layout);
        topThreeViewHolder.bind(topPlayer);
    }

    private void setupRecyclerView() {
        recyclerView = rootView.findViewById(R.id.playerList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        List<Player> remainingPlayers = new ArrayList<>(topPlayer.subList(3, topPlayer.size()));
        adapter = new PlayerAdapter(requireContext(), remainingPlayers);
        recyclerView.setAdapter(adapter);
    }
}