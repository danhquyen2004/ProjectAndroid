package com.example.tlupickleball.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlupickleball.R;
import com.example.tlupickleball.adapters.PlayerAdapter;
import com.example.tlupickleball.model.Player;

import java.util.ArrayList;
import java.util.List;

public class Solo_Rank extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_solo_rank);

        RecyclerView recyclerView = findViewById(R.id.playerList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Player> sampleData = new ArrayList<>();
        sampleData.add(new Player("Ahri", 2.1f, R.drawable.avatar_1));
        sampleData.add(new Player("Lux", 1.9f, R.drawable.avatar_1));
        sampleData.add(new Player("Jinx", 1.8f, R.drawable.avatar_1));
        sampleData.add(new Player("Vi", 1.6f, R.drawable.avatar_1));
        sampleData.add(new Player("Zed", 1.5f, R.drawable.avatar_1));

        PlayerAdapter adapter = new PlayerAdapter(sampleData);
        recyclerView.setAdapter(adapter);

        LinearLayout footer = findViewById(R.id.footer_navigation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            // Áp padding chỉ cho footer
            footer.setPadding(
                    footer.getPaddingLeft(),
                    footer.getPaddingTop(),
                    footer.getPaddingRight(),
                    systemBars.bottom  // chỉ áp bottom
            );
            return insets;
        });

    }
}