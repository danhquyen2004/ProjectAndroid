package com.example.tlupickleball.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.tlupickleball.R;
import com.example.tlupickleball.activities.base.BaseFragment;
import com.example.tlupickleball.adapters.UserRankAdapter;
import com.example.tlupickleball.model.Player;
import com.example.tlupickleball.model.User;
import com.example.tlupickleball.model.UserRank;
import com.example.tlupickleball.network.api_model.user.UserListResponse;
import com.example.tlupickleball.network.core.ApiClient;
import com.example.tlupickleball.network.service.UserService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;


public class Duo_Rank_Fragment extends Fragment {
    private RecyclerView recyclerView;
    private UserRankAdapter adapter;
    private List<UserRank> topUser;
    private View rootView;
    private TopThreeViewHolder topThreeViewHolder;
    UserService userService;
    private FragmentLoadingOverlay loadingOverlay;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_player_rank, container, false);
        loadingOverlay = new FragmentLoadingOverlay(rootView);

        userService = ApiClient.getClient(requireContext()).create(UserService.class);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);

        if (topUser == null) topUser = new ArrayList<>();

        fetchTopPlayers();
        setupTopThree();
        setupRecyclerView();

        swipeRefreshLayout.setOnRefreshListener(this::fetchTopPlayers);

        return rootView;
    }

    public void showLoading() {
        if (loadingOverlay != null) loadingOverlay.show();
    }

    public void hideLoading() {
        if (loadingOverlay != null) loadingOverlay.hide();
    }

    private void setupTopThree() {
        View top3Layout = rootView.findViewById(R.id.top3Layout);
        topThreeViewHolder = new TopThreeViewHolder(top3Layout);
        topThreeViewHolder.bind(topUser);
    }

    private void setupRecyclerView() {
        recyclerView = rootView.findViewById(R.id.playerList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        List<UserRank> remainingPlayers;
        if (topUser.size() > 3) {
            remainingPlayers = new ArrayList<>(topUser.subList(3, topUser.size()));
        } else {
            remainingPlayers = new ArrayList<>();
        }
        adapter = new UserRankAdapter(requireContext(), remainingPlayers);
        recyclerView.setAdapter(adapter);
    }
    // Example in your fragment or activity
    private void fetchTopPlayers() {
        if (!swipeRefreshLayout.isRefreshing()) {
            showLoading();
        }
        userService.getTopDuoRank().enqueue(new retrofit2.Callback<List<UserRank>>() {
            @Override
            public void onResponse(Call<List<UserRank>> call, retrofit2.Response<List<UserRank>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    topUser = response.body();
                    if (topUser == null) topUser = new ArrayList<>();
                    // Update top 3
                    topThreeViewHolder.bind(topUser);
                    // Update RecyclerView for remaining players
                    List<UserRank> remainingPlayers = new ArrayList<>();
                    if (topUser.size() > 3) {
                        remainingPlayers = new ArrayList<>(topUser.subList(3, topUser.size()));
                    }
                    adapter.setUsers(remainingPlayers);
                    adapter.notifyDataSetChanged();
                    hideLoading();
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    Toast.makeText(requireContext(), "Không có dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                    hideLoading();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<List<UserRank>> call, Throwable t) {
                Toast.makeText(requireContext(), "Connection error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                t.printStackTrace();
                hideLoading();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
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

        public void bind(List<UserRank> topPlayers) {
            if (topPlayers.size() >= 3) {
                setPlayerData(0, txtName1, txtScore1, top1Img, topPlayers);
                setPlayerData(1, txtName2, txtScore2, top2Img, topPlayers);
                setPlayerData(2, txtName3, txtScore3, top3Img, topPlayers);
            }
        }

        private void setPlayerData(int index, TextView name, TextView score, ImageView image, List<UserRank> users) {
            UserRank user = users.get(index);
            name.setText(user.getFullName());
            score.setText(String.valueOf(user.getPoint()));
            Glide.with(rootView.getContext())
                    .load(user.getAvatarUrl())
                    .placeholder(R.drawable.default_avatar)
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // Bỏ qua cache trên đĩa
                    .skipMemoryCache(true) // Bỏ qua cache trong bộ nhớ
                    .circleCrop()
                    .into(image);
        }
    }
}