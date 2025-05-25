package com.example.tlupickleball.activities;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tlupickleball.R;
import com.example.tlupickleball.adapters.PlayerAdapter;
import com.example.tlupickleball.adapters.RankAdapter;
import com.example.tlupickleball.model.Player;

import java.util.ArrayList;
import java.util.List;

public class Rank extends AppCompatActivity implements View.OnClickListener {
    ViewPager2 viewPager;
    ColorStateList def;
    TextView item1, item2, select;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        item1 = findViewById(R.id.item1);
        item1.setText("Cá nhân");
        item2 = findViewById(R.id.item2);
        item2.setText("Tổng quát");

        item1.setOnClickListener( this);
        item2.setOnClickListener( this);

        select = findViewById(R.id.select);
        def = item2.getTextColors();

        viewPager = findViewById(R.id.viewPager);
        FragmentManager manager = getSupportFragmentManager();
        RankAdapter rankAdapter = new RankAdapter(manager, getLifecycle());
        viewPager.setAdapter(rankAdapter);

        LinearLayout footer = findViewById(R.id.footer_navigation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            // Áp padding chỉ cho footer
            if(footer != null) {
                footer.setPadding(
                        footer.getPaddingLeft(),
                        footer.getPaddingTop(),
                        footer.getPaddingRight(),
                        systemBars.bottom  // chỉ áp bottom
                );
            }
            return insets;
        });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.item1) {
            select.animate().x(0).setDuration(100);
            item1.setTextColor(Color.WHITE);
            item2.setTextColor(def);
            viewPager.setCurrentItem(0);
        } else if (v.getId() == R.id.item2) {
            item1.setTextColor(def);
            item2.setTextColor(Color.WHITE);
            int size = item2.getWidth();
            select.animate().x(size).setDuration(100);
            viewPager.setCurrentItem(1);
        }
    }

    public static class TopThreeViewHolder extends AppCompatActivity{
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