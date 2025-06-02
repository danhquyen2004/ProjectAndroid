package com.example.tlupickleball.activities;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlupickleball.R;
import com.example.tlupickleball.adapters.ApprovePlayerAdapter;
import com.example.tlupickleball.adapters.PlayerAdapter;
import com.example.tlupickleball.fragments.Top_Rank_Fragment;
import com.example.tlupickleball.model.Player;

import java.util.ArrayList;
import java.util.List;

public class ApprovePlayer extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ApprovePlayerAdapter adapter;
    private List<Player> lstPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_approve_player);

        initData();
        setupRecyclerView();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initData() {
        lstPlayer = new ArrayList<>();
        lstPlayer.add(new Player("Ahri", "hihihaha@gmail.com", R.drawable.avatar_1));
        lstPlayer.add(new Player("Lux", "hihehh@gmail.com", R.drawable.avatar_1));
        lstPlayer.add(new Player("Jinx", "hiha@gmail.com", R.drawable.avatar_1));
        lstPlayer.add(new Player("Vi", "hihaha@gmail.com", R.drawable.avatar_1));
        lstPlayer.add(new Player("Zed", "hihoha@gmail.com", R.drawable.avatar_1));
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.playerList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ApprovePlayerAdapter(this, lstPlayer);
        recyclerView.setAdapter(adapter);
    }
}