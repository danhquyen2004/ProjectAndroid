package com.example.tlupickleball.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tlupickleball.R;
import com.example.tlupickleball.adapters.PlayerAdapter;
import com.example.tlupickleball.model.Player;

import java.util.ArrayList;
import java.util.List;


public class Top_Rank_Fragment extends Fragment {

    private RecyclerView recyclerView;
    private PlayerAdapter adapter;
    private List<Player> topPlayer;
    private View rootView;
    private TopThreeViewHolder topThreeViewHolder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_player_rank, container, false);

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
        topThreeViewHolder = new TopThreeViewHolder(top3Layout);
        topThreeViewHolder.bind(topPlayer);
    }

    private void setupRecyclerView() {
        recyclerView = rootView.findViewById(R.id.playerList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        List<Player> remainingPlayers = new ArrayList<>(topPlayer.subList(3, topPlayer.size()));
        adapter = new PlayerAdapter(requireContext(), remainingPlayers);
        recyclerView.setAdapter(adapter);
    }

    public static class TopThreeViewHolder extends AppCompatActivity {
        private TextView txtName1, txtName2, txtName3;
        private TextView txtScore1, txtScore2, txtScore3;
        private ImageView top1Img, top2Img, top3Img;
        private View rootView;

        public TopThreeViewHolder(View view) {
            rootView = view;
            initViews();
        }

        private void initViews() {
            txtName1 = rootView.findViewById(R.id.txt_name1);
            txtName2 = rootView.findViewById(R.id.txt_name2);
            txtName3 = rootView.findViewById(R.id.txt_name3);
            txtScore1 = rootView.findViewById(R.id.txt_score1);
            txtScore2 = rootView.findViewById(R.id.txt_score2);
            txtScore3 = rootView.findViewById(R.id.txt_score3);
            top1Img = rootView.findViewById(R.id.top1_img);
            top2Img = rootView.findViewById(R.id.top2_img);
            top3Img = rootView.findViewById(R.id.top3_img);
        }

        public void bind(List<Player> topPlayers) {
            if (topPlayers.size() >= 3) {
                setPlayerData(0, txtName1, txtScore1, top1Img, topPlayers);
                setPlayerData(1, txtName2, txtScore2, top2Img, topPlayers);
                setPlayerData(2, txtName3, txtScore3, top3Img, topPlayers);
            }
        }

        private void setPlayerData(int index, TextView name, TextView score, ImageView image, List<Player> players) {
            Player player = players.get(index);
            name.setText(player.getName());
            score.setText(String.valueOf(player.getSoloPoint()));
            image.setImageResource(player.getAvatarResourceId());
        }
    }
}