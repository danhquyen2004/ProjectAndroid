package com.example.tlupickleball.fragments;

import android.animation.LayoutTransition;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.tlupickleball.R;
import com.example.tlupickleball.adapters.DateAdapter;
import com.example.tlupickleball.adapters.MatchAdapter;
import com.example.tlupickleball.model.DateItem;
import com.example.tlupickleball.model.Match;
import com.example.tlupickleball.model.Matches;
import com.example.tlupickleball.model.Participant;
import com.example.tlupickleball.network.api_model.match.MatchResponse;
import com.example.tlupickleball.network.core.ApiClient;
import com.example.tlupickleball.network.core.SessionManager;
import com.example.tlupickleball.network.service.MatchService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Matches_Fragment extends Fragment implements DateAdapter.OnDateSelectedListener, MatchAdapter.OnMatchClickListener {

    private RecyclerView recyclerViewDates;
    private DateAdapter dateAdapter;
    private RecyclerView recyclerViewMatches;
    private MatchAdapter matchAdapter;
    private List<DateItem> datesList;
    private List<Matches> displayedMatchesList;
    private Map<String, List<Match>> allMatchesData;
    private TextView textViewSelectedDate;
    private LinearLayout filterStatusContainer;
    private TextView textFilter;
    private FloatingActionButton btnAddMatch;
    private TextView item1, item2, select;
    private FrameLayout tabContentFrame;
    private String currentViewType = "individual";
    private String currentSelectedDateKey;
    private String currentSelectedFilter = "Tất cả";
    private MatchService matchService;
    private String currentUserId;

    private ProgressBar progressBar;
    private View contentContainer;
    private SwipeRefreshLayout swipeRefreshLayout;

    private static final String KEY_CURRENT_DATE = "current_date_key";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            currentSelectedDateKey = savedInstanceState.getString(KEY_CURRENT_DATE);
        }

        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {

            if (bundle.getBoolean("didReturnFromDetail")) {
                if (btnAddMatch != null) {
                    btnAddMatch.setVisibility(View.VISIBLE);
                }
            }

            if (bundle.getBoolean("needsRefresh")) {
                String modifiedDateKey = bundle.getString("modifiedDateKey");
                if (modifiedDateKey != null) {
                    Log.d("Matches_Fragment", "Nhận được yêu cầu refresh cho ngày: " + modifiedDateKey);
                    String compositeKeyIndividual = modifiedDateKey + "_individual";
                    String compositeKeyClubWide = modifiedDateKey + "_club_wide";
                    allMatchesData.remove(compositeKeyIndividual);
                    allMatchesData.remove(compositeKeyClubWide);
                    if (modifiedDateKey.equals(currentSelectedDateKey)) {
                        Log.d("Matches_Fragment", "Ngày bị thay đổi là ngày đang xem, tải lại ngay lập tức.");
                        loadMatchesForSelectedDate(currentSelectedDateKey, currentViewType);
                    }
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_matches, container, false);

        recyclerViewDates = root.findViewById(R.id.recyclerViewDates);
        recyclerViewMatches = root.findViewById(R.id.recyclerViewMatches);
        textViewSelectedDate = root.findViewById(R.id.textViewSelectedDate);
        filterStatusContainer = root.findViewById(R.id.filterButton);
        textFilter = root.findViewById(R.id.textFilter);
        btnAddMatch = root.findViewById(R.id.btnAddMatch);
        item1 = root.findViewById(R.id.item1);
        item2 = root.findViewById(R.id.item2);
        select = root.findViewById(R.id.select);
        tabContentFrame = root.findViewById(R.id.tab_content_frame);
        progressBar = root.findViewById(R.id.progressBar);
        contentContainer = root.findViewById(R.id.contentContainer);
        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.d("Matches_Fragment", "Kéo để làm mới...");
            if (currentSelectedDateKey != null) {
                String compositeKeyIndividual = currentSelectedDateKey + "_individual";
                String compositeKeyClubWide = currentSelectedDateKey + "_club_wide";
                allMatchesData.remove(compositeKeyIndividual);
                allMatchesData.remove(compositeKeyClubWide);
            }
            loadMatchesForSelectedDate(currentSelectedDateKey, currentViewType);
        });

        textFilter.setText(currentSelectedFilter);

        matchService = ApiClient.getClient(getContext()).create(MatchService.class);
        currentUserId = SessionManager.getUid(getContext());
        allMatchesData = new HashMap<>();
        displayedMatchesList = new ArrayList<>();

        matchAdapter = new MatchAdapter(displayedMatchesList, this);
        recyclerViewMatches.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewMatches.setAdapter(matchAdapter);

        datesList = generateDates();
        recyclerViewDates.post(() -> {
            dateAdapter = new DateAdapter(datesList, this, recyclerViewDates.getWidth());
            recyclerViewDates.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            recyclerViewDates.setAdapter(dateAdapter);

            if (currentSelectedDateKey == null) {
                selectToday();
            } else {
                selectRestoredDate(currentSelectedDateKey);
            }
        });

        filterStatusContainer.setOnClickListener(v -> showStatusFilterDialog());
        btnAddMatch.setOnClickListener(v -> {
            if (btnAddMatch != null) btnAddMatch.setVisibility(View.GONE);
            getParentFragmentManager().beginTransaction()
                    .add(R.id.matchFragmentRoot, new AddMatch_Fragment())
                    .addToBackStack(null).commit();
        });

        setupTabLayout();
        return root;
    }

    @Override
    public void onMatchClicked(Matches match) {
        if (btnAddMatch != null) {
            btnAddMatch.setVisibility(View.GONE);
        }
        AddMatch_Fragment detailFragment = new AddMatch_Fragment();
        Bundle args = new Bundle();
        args.putString("match_id", match.getMatchId());
        detailFragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .add(R.id.matchFragmentRoot, detailFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CURRENT_DATE, currentSelectedDateKey);
    }

    // Trong file Matches_Fragment.java

    @Override
    public void onResume() {
        super.onResume();

        if (btnAddMatch != null) {
            btnAddMatch.setVisibility(View.VISIBLE);
        }
        updateTabSelection(currentViewType);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (btnAddMatch != null) {
            btnAddMatch.setVisibility(View.GONE);
        }
    }

    private void selectRestoredDate(String dateKey) {
        try {
            SimpleDateFormat ymdFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date restoredDate = ymdFormat.parse(dateKey);
            SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
            SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", new Locale("vi", "VN"));
            String restoredDay = dayFormat.format(restoredDate);
            String restoredMonth = monthFormat.format(restoredDate);

            for (int i = 0; i < datesList.size(); i++) {
                DateItem item = datesList.get(i);
                if (item.getDayOfMonth().equals(restoredDay) && item.getMonth().equals(restoredMonth)) {
                    dateAdapter.setSelectedPosition(i);
                    recyclerViewDates.scrollToPosition(i);
                    onDateSelected(item);
                    return;
                }
            }
        } catch (Exception e) {
            selectToday();
        }
    }

    private void setupTabLayout() {
        if (item1 != null && item1.getParent() instanceof LinearLayout) {
            ((LinearLayout) item1.getParent()).getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        }
        if (item1 != null) {
            item1.setText("Cá nhân");
            item1.setOnClickListener(v -> {
                if (!currentViewType.equals("individual")) {
                    currentViewType = "individual";
                    updateTabSelection(currentViewType);
                    renderMatchesForCurrentState();
                }
            });
        }
        if (item2 != null) {
            item2.setText("Tổng quát");
            item2.setOnClickListener(v -> {
                if (!currentViewType.equals("club_wide")) {
                    currentViewType = "club_wide";
                    updateTabSelection(currentViewType);
                    renderMatchesForCurrentState();
                }
            });
        }
        updateTabSelection(currentViewType);
    }

    private void updateTabSelection(String selectedType) {
        if (tabContentFrame == null || select == null || item1 == null || item2 == null) return;
        if (tabContentFrame.getWidth() == 0) {
            tabContentFrame.post(() -> updateTabSelection(selectedType));
            return;
        }
        int tabWidth = tabContentFrame.getWidth() / 2;
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) select.getLayoutParams();
        params.width = tabWidth;
        if (selectedType.equals("individual")) {
            params.setMarginStart(0);
            item1.setTextColor(Color.WHITE);
            item2.setTextColor(Color.BLACK);
        } else {
            params.setMarginStart(tabWidth);
            item1.setTextColor(Color.BLACK);
            item2.setTextColor(Color.WHITE);
        }
        select.setLayoutParams(params);
    }

    private List<DateItem> generateDates() {
        List<DateItem> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -10);
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", new Locale("vi", "VN"));
        for (int i = 0; i < 30; i++) {
            dates.add(new DateItem(dayFormat.format(calendar.getTime()), monthFormat.format(calendar.getTime())));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return dates;
    }

    private void selectToday() {
        Calendar todayCalendar = Calendar.getInstance();
        String currentDay = new SimpleDateFormat("dd", Locale.getDefault()).format(todayCalendar.getTime());
        String currentMonth = new SimpleDateFormat("MMM", new Locale("vi", "VN")).format(todayCalendar.getTime());
        for (int i = 0; i < datesList.size(); i++) {
            if (datesList.get(i).getDayOfMonth().equals(currentDay) && datesList.get(i).getMonth().equals(currentMonth)) {
                if (dateAdapter != null) {
                    dateAdapter.setSelectedPosition(i);
                    recyclerViewDates.scrollToPosition(i);
                }
                onDateSelected(datesList.get(i));
                break;
            }
        }
    }

    @Override
    public void onDateSelected(DateItem date) {
        String selectedDay = date.getDayOfMonth();
        String selectedMonthDisplay = date.getMonth().substring(0, 1).toUpperCase(Locale.ROOT) + date.getMonth().substring(1);
        textViewSelectedDate.setText(String.format("%s %s", selectedDay, selectedMonthDisplay));
        try {
            Calendar selectedCal = Calendar.getInstance();
            selectedCal.setTime(new SimpleDateFormat("MMM", new Locale("vi", "VN")).parse(date.getMonth()));
            selectedCal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.getDayOfMonth()));
            selectedCal.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
            currentSelectedDateKey = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedCal.getTime());
        } catch (Exception e) {
            Log.e("DateSelection", "Lỗi khi parse ngày tháng", e);
        }
        renderMatchesForCurrentState();
    }

    private void showStatusFilterDialog() {
        final String[] statusOptions = {"Tất cả", "Sắp diễn ra", "Đang diễn ra", "Đã kết thúc"};
        new AlertDialog.Builder(getContext())
                .setTitle("Lọc trạng thái trận đấu")
                .setItems(statusOptions, (dialog, which) -> {
                    currentSelectedFilter = statusOptions[which];
                    textFilter.setText(currentSelectedFilter);
                    filterMatchesByStatus();
                })
                .show();
    }

    private void renderMatchesForCurrentState() {
        if (currentSelectedDateKey == null || currentViewType == null) {
            return;
        }
        String compositeKey = currentSelectedDateKey + "_" + currentViewType;
        if (allMatchesData.containsKey(compositeKey)) {
            Log.d("Matches_Fragment", "Dữ liệu đã có trong cache, hiển thị ngay.");
            convertAndDisplayMatches(allMatchesData.get(compositeKey));
        } else {
            Log.d("Matches_Fragment", "Dữ liệu chưa có trong cache, bắt đầu tải mới.");
            loadMatchesForSelectedDate(currentSelectedDateKey, currentViewType);
        }
    }

    private void loadMatchesForSelectedDate(String dateKey, String viewType) {
        if (dateKey == null || viewType == null) {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            return;
        }

        String compositeKey = dateKey + "_" + viewType;

        if (allMatchesData.containsKey(compositeKey)) {
            convertAndDisplayMatches(allMatchesData.get(compositeKey));
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        } else {
            if (!swipeRefreshLayout.isRefreshing()) {
                progressBar.setVisibility(View.VISIBLE);
                contentContainer.setVisibility(View.INVISIBLE);
                btnAddMatch.setVisibility(View.GONE);
            }

            Call<MatchResponse> call = viewType.equals("individual")
                    ? matchService.getMatchesByDayAndUser(dateKey, currentUserId, 1, 50)
                    : matchService.getMatchesByDay(dateKey, 1, 50);

            call.enqueue(new Callback<MatchResponse>() {
                @Override
                public void onResponse(@NonNull Call<MatchResponse> call, @NonNull Response<MatchResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    contentContainer.setVisibility(View.VISIBLE);
                    btnAddMatch.setVisibility(View.VISIBLE);

                    if (response.isSuccessful() && response.body() != null) {
                        List<Match> fetchedMatches = response.body().getMatches();
                        allMatchesData.put(compositeKey, fetchedMatches);
                        convertAndDisplayMatches(fetchedMatches);
                        startBackgroundPrefetching();
                    } else {
                        Toast.makeText(getContext(), "Không thể tải trận đấu", Toast.LENGTH_SHORT).show();
                        if (matchAdapter != null) matchAdapter.updateMatches(new ArrayList<>());
                    }
                }
                @Override
                public void onFailure(@NonNull Call<MatchResponse> call, @NonNull Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    contentContainer.setVisibility(View.VISIBLE);
                    btnAddMatch.setVisibility(View.VISIBLE);

                    Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
                    if (matchAdapter != null) matchAdapter.updateMatches(new ArrayList<>());
                }
            });
        }
    }

    private void convertAndDisplayMatches(List<Match> rawMatches) {
        List<Matches> convertedMatches = new ArrayList<>();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
//        apiDateFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));

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
                    time = timeFormat.format(apiDateFormat.parse(rawMatch.getStartTime()));
                } catch (Exception e) { /* ignore */ }
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
        this.displayedMatchesList.clear();
        this.displayedMatchesList.addAll(convertedMatches);
        filterMatchesByStatus();
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

    private void prefetchDataForDate(String dateKey) {
        if (dateKey == null || getContext() == null) return;

        String[] viewTypes = {"individual", "club_wide"};

        for (String viewType : viewTypes) {
            String compositeKey = dateKey + "_" + viewType;
            if (!allMatchesData.containsKey(compositeKey)) {
                Log.d("Matches_Prefetch", "Bắt đầu tải ngầm cho key: " + compositeKey);

                Call<MatchResponse> call = viewType.equals("individual")
                        ? matchService.getMatchesByDayAndUser(dateKey, currentUserId, 1, 50)
                        : matchService.getMatchesByDay(dateKey, 1, 50);

                call.enqueue(new Callback<MatchResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<MatchResponse> call, @NonNull Response<MatchResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d("Matches_Prefetch", "Tải ngầm thành công cho key: " + compositeKey);
                            allMatchesData.put(compositeKey, response.body().getMatches());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<MatchResponse> call, @NonNull Throwable t) {
                        Log.e("Matches_Prefetch", "Tải ngầm thất bại cho key: " + compositeKey, t);
                    }
                });
            }
        }
    }

    private void startBackgroundPrefetching() {
        if (currentSelectedDateKey == null) return;

        SimpleDateFormat ymdFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date currentDate = ymdFormat.parse(currentSelectedDateKey);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);

            // Tải trước 2 ngày tiếp theo
            for (int i = 1; i <= 2; i++) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                prefetchDataForDate(ymdFormat.format(calendar.getTime()));
            }

            // Reset lại calendar về ngày hiện tại và tải trước 2 ngày trước đó
            calendar.setTime(currentDate);
            for (int i = 1; i <= 2; i++) {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                prefetchDataForDate(ymdFormat.format(calendar.getTime()));
            }

        } catch (Exception e) {
            Log.e("Matches_Prefetch", "Lỗi khi bắt đầu tải ngầm", e);
        }
    }

    private void filterMatchesByStatus() {
        // Nếu adapter chưa tồn tại, không làm gì cả.
        if (matchAdapter == null) {
            return;
        }
        List<Matches> filteredList = new ArrayList<>();

        if ("Tất cả".equals(currentSelectedFilter)) {
            filteredList.addAll(displayedMatchesList);
        } else {
            for (Matches match : displayedMatchesList) {
                if (match.getMatchStatus().trim().equals(currentSelectedFilter.trim())) {
                    filteredList.add(match);
                }
            }
        }
        matchAdapter.updateMatches(filteredList);
    }
}