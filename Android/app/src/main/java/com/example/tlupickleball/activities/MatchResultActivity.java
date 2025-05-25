package com.example.tlupickleball.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlupickleball.R;
import com.example.tlupickleball.adapters.MatchResultAdapter;
import com.example.tlupickleball.model.MatchResult;

import java.util.ArrayList;
import java.util.List;

public class MatchResultActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MatchResultAdapter adapter;
    private List<MatchResult> matchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.match_final_person);

        recyclerView = findViewById(R.id.recyclerViewMatchResult);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        matchList = new ArrayList<>();
        loadDummyData();

        adapter = new MatchResultAdapter(matchList);
        recyclerView.setAdapter(adapter);
    }

    private void loadDummyData() {
        matchList.add(new MatchResult("Ahri", "Lux", "2 - 0"));
        matchList.add(new MatchResult("Jinx", "Vi", "1 - 2"));
        matchList.add(new MatchResult("Zed", "Yasuo", "0 - 2"));
        matchList.add(new MatchResult("Vayne", "Teemo", "2 - 1"));
    }
}
