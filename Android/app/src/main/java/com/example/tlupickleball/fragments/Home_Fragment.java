package com.example.tlupickleball.fragments;

import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toolbar;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.tlupickleball.R;
import com.example.tlupickleball.activities.MainActivity;
import com.example.tlupickleball.activities.UserActivity;
import com.example.tlupickleball.model.User;
import com.example.tlupickleball.network.api_model.user.UserRankAndStatus;
import com.example.tlupickleball.network.core.SessionManager;
import com.example.tlupickleball.network.service.UserService;
import com.google.android.material.navigation.NavigationView;
import com.example.tlupickleball.adapters.MatchAdapter;
import com.example.tlupickleball.model.Match;
import com.example.tlupickleball.model.Matches;
import com.example.tlupickleball.model.Participant;
import com.example.tlupickleball.network.api_model.match.MatchResponse;
import com.example.tlupickleball.network.core.ApiClient;
import com.example.tlupickleball.network.service.MatchService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Home_Fragment extends Fragment implements MatchAdapter.OnMatchClickListener {
    TextView txtName, txtSoloPoint, txtDoublePoint, txtStatusFund;
    private View rootView;

    // ===== BIẾN MỚI ĐƯỢC THÊM VÀO =====
    private RecyclerView recyclerViewTodayMatches;
    private MatchAdapter matchAdapter;
    private List<Matches> todayMatchesList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;

    // Thêm biến cho container và progress bar
    private View contentContainer;
    private ProgressBar progressBar;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Giữ nguyên việc inflate layout
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        // Ví dụ: Button mở drawer
//        View btnOpenDrawer = rootView.findViewById(R.id.btnMenu);
//        btnOpenDrawer.setOnClickListener(v -> {
//            // Truy cập Drawer từ MainActivity
//            DrawerLayout drawerLayout = ((UserActivity) requireActivity()).drawerLayout;
//            if (drawerLayout != null) {
//                drawerLayout.openDrawer(GravityCompat.END);
//            }
//        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo các view mới
        contentContainer = view.findViewById(R.id.contentContainerHome);
        progressBar = view.findViewById(R.id.progressBarHome);

        txtName = rootView.findViewById(R.id.id_Name);
        txtSoloPoint = rootView.findViewById(R.id.id_SingleRank);
        txtDoublePoint = rootView.findViewById(R.id.id_DuoRank);
        txtStatusFund = rootView.findViewById(R.id.id_status_fund);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(this::fetchPlayerInfor);

        View btnOpenDrawer = rootView.findViewById(R.id.btnMenu);
        btnOpenDrawer.setOnClickListener(v -> {
            // Truy cập Drawer từ Activity chứa fragment này
            if (getActivity() instanceof UserActivity) {
                DrawerLayout drawerLayout = ((UserActivity) requireActivity()).drawerLayout;
                if (drawerLayout != null) {
                    drawerLayout.openDrawer(GravityCompat.END);
                }
            }
        });

        // ===== LOGIC MỚI ĐỂ HIỂN THỊ DANH SÁCH TRẬN ĐẤU =====
        // Khởi tạo RecyclerView và Adapter
        recyclerViewTodayMatches = view.findViewById(R.id.recyclerViewTodayMatches);
        setupRecyclerView();

        // Gọi API để lấy dữ liệu
//        fetchTodayMatches();
        fetchPlayerInfor();
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        // Luôn tải lại dữ liệu khi màn hình được hiển thị
//        // fetchPlayerInfor();
//    }

    // ===== CÁC PHƯƠNG THỨC MỚI ĐƯỢC THÊM VÀO =====

    private void setupRecyclerView() {
        // Sử dụng lại MatchAdapter đã có
        matchAdapter = new MatchAdapter(todayMatchesList, this);
        recyclerViewTodayMatches.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewTodayMatches.setAdapter(matchAdapter);
    }

//    private void fetchData() {
//        // Gọi các phương thức để lấy dữ liệu người dùng và trận đấu
//        fetchPlayerInfor();
//        fetchTodayMatches();
//        swipeRefreshLayout.setRefreshing(false);
//    }

    private void fetchPlayerInfor() {
        if (getContext() == null) return;

        if (!swipeRefreshLayout.isRefreshing()) {
            if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
            if (contentContainer != null) contentContainer.setVisibility(View.INVISIBLE); // Dùng INVISIBLE để layout không bị giật
        }

        // Tạo service và gọi API
        UserService userService = ApiClient.getClient(getContext()).create(UserService.class);
        // Cập nhật tên người dùng
        Call<User> callUser = userService.getUserProfileById(SessionManager.getUid(requireContext()));
        callUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    txtName.setText(response.body().getFullName());
                } else {
                    Toast.makeText(getContext(), "Không có dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng(User): " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Home_Fragment_API", "API call failed", t);
            }
        });

        Call<UserRankAndStatus> call = userService.getUserProfileByIdInHome(SessionManager.getUid(requireContext()));
        call.enqueue(new Callback<UserRankAndStatus>() {
            @Override
            public void onResponse(@NonNull Call<UserRankAndStatus> call, @NonNull Response<UserRankAndStatus> response) {
                if (response.isSuccessful() && response.body() != null) {
                    txtSoloPoint.setText(String.valueOf(response.body().getSingleRank()));
                    txtDoublePoint.setText(String.valueOf(response.body().getDoubleRank()));
                    txtStatusFund.setText(response.body().getFundStatus());
                    fetchTodayMatches();
                } else {
                    Toast.makeText(getContext(), "Không có dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserRankAndStatus> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng(UserRankAndStatus): " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Home_Fragment_API", "API call failed", t);
                fetchPlayerInfor(); // Gọi lại để thử tải lại dữ liệu
            }
        });
    }

    private void fetchTodayMatches() {
        if (getContext() == null) return;

        // BẮT ĐẦU HIỂN THỊ LOADING
//        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
//        if (contentContainer != null) contentContainer.setVisibility(View.INVISIBLE); // Dùng INVISIBLE để layout không bị giật

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDateString = dateFormat.format(new Date());

        // Tạo service và gọi API
        MatchService matchService = ApiClient.getClient(getContext()).create(MatchService.class);
        Call<MatchResponse> call = matchService.getMatchesByDay(todayDateString, 1, 50);

        call.enqueue(new Callback<MatchResponse>() {
            @Override
            public void onResponse(@NonNull Call<MatchResponse> call, @NonNull Response<MatchResponse> response) {
                // KẾT THÚC LOADING, HIỂN THỊ NỘI DUNG
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (contentContainer != null) contentContainer.setVisibility(View.VISIBLE);

                if (response.isSuccessful() && response.body() != null) {
                    List<Matches> convertedMatches = convertApiMatchesToDisplayable(response.body().getMatches());
                    matchAdapter.updateMatches(convertedMatches);
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    Toast.makeText(getContext(), "Không thể tải danh sách trận đấu", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MatchResponse> call, @NonNull Throwable t) {
                // KẾT THÚC LOADING, HIỂN THỊ NỘI DUNG (dù là rỗng)
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (contentContainer != null) contentContainer.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);

                Toast.makeText(getContext(), "Lỗi mạng(Matches): " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Home_Fragment_API", "API call failed", t);

                fetchTodayMatches(); // Gọi lại để thử tải lại dữ liệu
            }
        });
    }

    private List<Matches> convertApiMatchesToDisplayable(List<Match> rawMatches) {
        List<Matches> convertedMatches = new ArrayList<>();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        // Bỏ setTimeZone để dùng giờ địa phương (đã sửa ở các bước trước)

        for (Match rawMatch : rawMatches) {
            List<Participant> team1 = new ArrayList<>();
            List<Participant> team2 = new ArrayList<>();
            if (rawMatch.getParticipants() != null) {
                for (Participant p : rawMatch.getParticipants()) {
                    if (p.getTeam() == 1) team1.add(p);
                    else team2.add(p);
                }
            }
            Collections.sort(team1, Comparator.comparing(Participant::getFullName));
            Collections.sort(team2, Comparator.comparing(Participant::getFullName));

            String p1Name = !team1.isEmpty() ? team1.get(0).getFullName() : "";
            if (team1.size() > 1) p1Name += " & " + team1.get(1).getFullName();

            String p2Name = !team2.isEmpty() ? team2.get(0).getFullName() : "";
            if (team2.size() > 1) p2Name += " & " + team2.get(1).getFullName();

            String p1Avatar1 = !team1.isEmpty() ? team1.get(0).getAvatarUrl() : null;
            String p1Avatar2 = team1.size() > 1 ? team1.get(1).getAvatarUrl() : null;
            String p2Avatar1 = !team2.isEmpty() ? team2.get(0).getAvatarUrl() : null;
            String p2Avatar2 = team2.size() > 1 ? team2.get(1).getAvatarUrl() : null;

            String time = "N/A";
            if (rawMatch.getStartTime() != null) {
                try {
                    // Dùng giờ địa phương để hiển thị
                    Date parsedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault()).parse(rawMatch.getStartTime());
                    time = timeFormat.format(parsedDate);
                } catch (Exception e) {
                    try { // Fallback cho trường hợp có 'Z'
                        apiDateFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                        time = timeFormat.format(apiDateFormat.parse(rawMatch.getStartTime()));
                    } catch(Exception e2) {/* ignore */}
                }
            }

            boolean isDoubles = rawMatch.getParticipants() != null && rawMatch.getParticipants().size() > 2;

            convertedMatches.add(new Matches(
                    rawMatch.getMatchId(),
                    p1Name, p2Name,
                    p1Avatar1, p1Avatar2, p2Avatar1, p2Avatar2,
                    rawMatch.getTeam1Wins() + "-" + rawMatch.getTeam2Wins(),
                    time,
                    mapApiStatusToDisplayStatus(rawMatch.getStatus()),
                    isDoubles
            ));
        }
        return convertedMatches;
    }

    private String mapApiStatusToDisplayStatus(String apiStatus) {
        if (apiStatus == null) return "";
        switch (apiStatus) {
            case "finished": return "Đã kết thúc";
            case "upcoming":
            case "pending": return "Sắp diễn ra";
            case "ongoing":
            case "in_progress": return "Đang diễn ra";
            default: return apiStatus;
        }
    }

    @Override
    public void onMatchClicked(Matches match) {
        AddMatch_Fragment detailFragment = new AddMatch_Fragment();
        Bundle args = new Bundle();
        args.putString("match_id", match.getMatchId());
        detailFragment.setArguments(args);

        // Chỗ này bạn cần xem lại R.id.main có đúng là id của container chính trong UserActivity không
        getParentFragmentManager().beginTransaction()
                .replace(R.id.main, detailFragment)
                .addToBackStack(null)
                .commit();
    }
}
