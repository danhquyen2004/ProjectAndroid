package com.example.tlupickleball.fragments;

import android.animation.LayoutTransition;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tlupickleball.R;
import com.example.tlupickleball.adapters.DateAdapter;
import com.example.tlupickleball.adapters.MatchAdapter;
import com.example.tlupickleball.model.DateItem;
import com.example.tlupickleball.model.Matches;

import com.example.tlupickleball.network.core.ApiClient;
import com.example.tlupickleball.network.service.MatchService;
import com.example.tlupickleball.network.api_model.match.MatchResponse;
import com.example.tlupickleball.model.Match;
import com.example.tlupickleball.model.Participant;
import com.example.tlupickleball.network.core.SessionManager;

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

import android.app.AlertDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Matches_Fragment extends Fragment implements DateAdapter.OnDateSelectedListener {

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
    private String currentMatchType = "individual";
    private String currentViewType = "individual";

    private String currentSelectedDateKey;
    private String currentSelectedFilter = "Tất cả";

    private MatchService matchService;
    private String currentUserId;


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

        matchService = ApiClient.getClient(getContext()).create(MatchService.class);
        currentUserId = SessionManager.getUid(getContext());


        allMatchesData = new HashMap<>();
        displayedMatchesList = new ArrayList<>();

        datesList = generateDates();
        recyclerViewDates.post(() -> {
            int recyclerViewWidth = recyclerViewDates.getWidth();
            dateAdapter = new DateAdapter(datesList, this, recyclerViewWidth);
            recyclerViewDates.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            recyclerViewDates.setAdapter(dateAdapter);
            selectToday();
        });

        matchAdapter = new MatchAdapter(displayedMatchesList);
        recyclerViewMatches.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewMatches.setAdapter(matchAdapter);

        filterStatusContainer.setOnClickListener(v -> showStatusFilterDialog());

        btnAddMatch.setOnClickListener(v -> {
            if (btnAddMatch != null) {
                btnAddMatch.setVisibility(View.GONE);
            }
            AddMatch_Fragment addMatchFragment = new AddMatch_Fragment();
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.matchFragmentRoot, addMatchFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        setupTabLayout();

        return root;
    }

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
                    if (currentSelectedDateKey != null) {
                        loadMatchesForSelectedDate(currentSelectedDateKey, currentViewType);
                    }
                }
            });
        }

        if (item2 != null) {
            item2.setText("Tổng quát");
            item2.setOnClickListener(v -> {
                if (!currentViewType.equals("club_wide")) {
                    currentViewType = "club_wide";
                    updateTabSelection(currentViewType);
                    if (currentSelectedDateKey != null) {
                        loadMatchesForSelectedDate(currentSelectedDateKey, currentViewType);
                    }
                }
            });
        }

        updateTabSelection(currentViewType);
    }

    private void updateTabSelection(String selectedType) {
        if (tabContentFrame == null || select == null || item1 == null || item2 == null) {
            Log.e("MatchesFragment", "Tab layout views not initialized.");
            return;
        }

        if (tabContentFrame.getWidth() == 0) {
            tabContentFrame.post(() -> updateTabSelection(selectedType));
            return;
        }

        int tabWidth = tabContentFrame.getWidth() / 2;
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) select.getLayoutParams();
        params.width = tabWidth;

        if (selectedType.equals("individual")) {
            params.setMarginStart(0);
            select.setLayoutParams(params);
            item1.setTextColor(Color.WHITE);
            item2.setTextColor(Color.BLACK);
        } else if (selectedType.equals("club_wide")) {
            params.setMarginStart(tabWidth);
            select.setLayoutParams(params);
            item1.setTextColor(Color.BLACK);
            item2.setTextColor(Color.WHITE);
        }
    }

    private List<DateItem> generateDates() {
        List<DateItem> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -10);

        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", new Locale("vi", "VN"));
        SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (int i = 0; i < 30; i++) {
            String dayOfMonth = dayFormat.format(calendar.getTime());
            String month = monthFormat.format(calendar.getTime());
            DateItem dateItem = new DateItem(dayOfMonth, month);
            dates.add(dateItem);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return dates;
    }

    private void selectToday() {
        Calendar todayCalendar = Calendar.getInstance();
        SimpleDateFormat sdfDay = new SimpleDateFormat("dd", Locale.getDefault());
        SimpleDateFormat sdfMonth = new SimpleDateFormat("MMM", new Locale("vi", "VN"));

        String currentDay = sdfDay.format(todayCalendar.getTime());
        String currentMonth = sdfMonth.format(todayCalendar.getTime());

        for (int i = 0; i < datesList.size(); i++) {
            if (datesList.get(i).getDayOfMonth().equals(currentDay) && datesList.get(i).getMonth().equals(currentMonth)) {
                dateAdapter.setSelectedPosition(i);
                recyclerViewDates.scrollToPosition(i);
                onDateSelected(datesList.get(i));
                break;
            }
        }
    }

    @Override
    public void onDateSelected(DateItem date) {
        String selectedDay = date.getDayOfMonth();
        String selectedMonthDisplay = date.getMonth().substring(0, 1).toUpperCase() + date.getMonth().substring(1);
        textViewSelectedDate.setText(selectedDay + " " + selectedMonthDisplay);

        Calendar selectedCal = Calendar.getInstance();
        SimpleDateFormat monthParseFormat = new SimpleDateFormat("MMM", new Locale("vi", "VN"));
        try {
            selectedCal.setTime(monthParseFormat.parse(date.getMonth()));
            selectedCal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.getDayOfMonth()));
            Calendar now = Calendar.getInstance();
            selectedCal.set(Calendar.YEAR, now.get(Calendar.YEAR));
        } catch (java.text.ParseException e) {
            Log.e("MatchesFragment", "Error parsing month for date selection: " + e.getMessage());
            selectedCal = Calendar.getInstance();
        }

        SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        currentSelectedDateKey = fullDateFormat.format(selectedCal.getTime());

        loadMatchesForSelectedDate(currentSelectedDateKey, currentViewType);
    }

    private void showStatusFilterDialog() {
        final String[] statusOptions = {"Tất cả", "Sắp diễn ra", "Đang diễn ra", "Đã kết thúc"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Lọc trạng thái trận đấu");
        builder.setItems(statusOptions, (dialog, which) -> {
            currentSelectedFilter = statusOptions[which];
            textFilter.setText(currentSelectedFilter);
            filterMatchesByStatus(currentSelectedFilter);
        });
        builder.show();
    }

    private void loadMatchesForSelectedDate(String dateKey, String viewType) {
        String compositeKey = dateKey + "_" + viewType;

        if (allMatchesData.containsKey(compositeKey)) {
            List<Match> cachedMatches = allMatchesData.get(compositeKey);
            Log.d("MatchesFragment", "Found " + cachedMatches.size() + " matches for " + compositeKey + " from cache.");
            convertAndDisplayMatches(cachedMatches, currentMatchType);
        } else {
            Log.d("MatchesFragment", "Fetching matches for " + compositeKey + " from API.");
            int page = 1;
            int pageSize = 50;

            Call<MatchResponse> call;
            if (viewType.equals("individual") && currentUserId != null) {
                call = matchService.getMatchesByDayAndUser(dateKey, currentUserId, page, pageSize);
            } else {
                call = matchService.getMatchesByDay(dateKey, page, pageSize);
            }

            call.enqueue(new Callback<MatchResponse>() {
                @Override
                public void onResponse(Call<MatchResponse> call, Response<MatchResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Match> fetchedMatches = response.body().getMatches();
                        allMatchesData.put(compositeKey, fetchedMatches);
                        convertAndDisplayMatches(fetchedMatches, currentMatchType);
                        Log.d("MatchesFragment", "Fetched " + fetchedMatches.size() + " matches for " + compositeKey + " from API.");
                    } else {
                        Log.e("MatchesFragment", "API call failed: " + response.message());
                        Toast.makeText(getContext(), "Failed to load matches: " + response.message(), Toast.LENGTH_SHORT).show();
                        displayedMatchesList.clear();
                        matchAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<MatchResponse> call, Throwable t) {
                    Log.e("MatchesFragment", "API call error: " + t.getMessage(), t);
                    Toast.makeText(getContext(), "Error loading matches: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    displayedMatchesList.clear();
                    matchAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    /**
     * Converts raw API Match objects to displayable Matches objects and updates the UI.
     * @param rawMatches The list of raw Match objects from the API.
     * @param matchType The current selected match type ("individual" or "doubles").
     */
    private void convertAndDisplayMatches(List<Match> rawMatches, String matchType) {
        List<Matches> convertedMatches = new ArrayList<>();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        // Define format for API's ISO 8601 timestamp
        SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        apiDateFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC")); // API time is UTC

        for (Match rawMatch : rawMatches) {
            String player1DisplayName = "";
            String player2DisplayName = "";
            String scoreDisplay = rawMatch.getTeam1Wins() + "-" + rawMatch.getTeam2Wins();
            String matchTimeDisplay = "N/A";
            String matchStatusDisplay = mapApiStatusToDisplayStatus(rawMatch.getStatus());

            String player1AvatarUrl1 = null;
            String player1AvatarUrl2 = null;
            String player2AvatarUrl1 = null;
            String player2AvatarUrl2 = null;

            List<Participant> team1Participants = new ArrayList<>();
            List<Participant> team2Participants = new ArrayList<>();

            for (Participant p : rawMatch.getParticipants()) {
                if (p.getTeam() == 1) {
                    team1Participants.add(p);
                } else if (p.getTeam() == 2) {
                    team2Participants.add(p);
                }
            }

            // Sort participants by full name to ensure consistent order for team display names and avatar assignment
            Collections.sort(team1Participants, Comparator.comparing(Participant::getFullName));
            Collections.sort(team2Participants, Comparator.comparing(Participant::getFullName));

            boolean currentMatchIsDoubles = false; // Mặc định là đơn

            if (matchType.equals("individual")) {
                if (!team1Participants.isEmpty()) {
                    player1DisplayName = team1Participants.get(0).getFullName();
                    player1AvatarUrl1 = team1Participants.get(0).getAvatarUrl(); // Lấy URL avatar
                }
                if (!team2Participants.isEmpty()) {
                    player2DisplayName = team2Participants.get(0).getFullName();
                    player2AvatarUrl1 = team2Participants.get(0).getAvatarUrl(); // Lấy URL avatar
                }
                currentMatchIsDoubles = false; // Xác định lại loại trận đấu
            } else if (matchType.equals("doubles")) {
                if (team1Participants.size() >= 2 && team2Participants.size() >= 2) {
                    List<String> team1Names = new ArrayList<>();
                    team1Names.add(team1Participants.get(0).getFullName());
                    team1Names.add(team1Participants.get(1).getFullName());
                    player1DisplayName = String.join(" & ", team1Names);

                    player1AvatarUrl1 = team1Participants.get(0).getAvatarUrl(); // Lấy URL avatar người 1 đội 1
                    player1AvatarUrl2 = team1Participants.get(1).getAvatarUrl(); // Lấy URL avatar người 2 đội 1

                    List<String> team2Names = new ArrayList<>();
                    team2Names.add(team2Participants.get(0).getFullName());
                    team2Names.add(team2Participants.get(1).getFullName());
                    player2DisplayName = String.join(" & ", team2Names);

                    player2AvatarUrl1 = team2Participants.get(0).getAvatarUrl(); // Lấy URL avatar người 1 đội 2
                    player2AvatarUrl2 = team2Participants.get(1).getAvatarUrl(); // Lấy URL avatar người 2 đội 2

                    currentMatchIsDoubles = true; // Xác định lại loại trận đấu
                } else {
                    // If it's supposed to be doubles but doesn't have 2 players per team,
                    // you might want to skip this match or handle it differently.
                    // For now, it will be treated as single if not enough participants.
                    Log.w("MatchesFragment", "Doubles match expected but not enough participants. Match ID: " + rawMatch.getMatchId());
                    // Fallback to singles display if not enough participants for doubles
                    if (!team1Participants.isEmpty()) {
                        player1DisplayName = team1Participants.get(0).getFullName();
                        player1AvatarUrl1 = team1Participants.get(0).getAvatarUrl();
                    }
                    if (!team2Participants.isEmpty()) {
                        player2DisplayName = team2Participants.get(0).getFullName();
                        player2AvatarUrl1 = team2Participants.get(0).getAvatarUrl();
                    }
                    currentMatchIsDoubles = false; // Treat as single for display
                }
            }

            // Parse and format match time
            if (rawMatch.getStartTime() != null && !rawMatch.getStartTime().isEmpty()) {
                try {
                    Date date = apiDateFormat.parse(rawMatch.getStartTime());
                    matchTimeDisplay = timeFormat.format(date); // Format to local time
                } catch (java.text.ParseException e) {
                    Log.e("MatchesFragment", "Error parsing match start time: " + rawMatch.getStartTime(), e);
                }
            }

            // Tạo đối tượng Matches với các URL avatar và trạng thái đấu đôi
            convertedMatches.add(new Matches(player1DisplayName, player2DisplayName,
                    player1AvatarUrl1, player1AvatarUrl2,
                    player2AvatarUrl1, player2AvatarUrl2,
                    scoreDisplay, matchTimeDisplay, matchStatusDisplay,
                    currentMatchIsDoubles));
        }

        displayedMatchesList.clear();
        displayedMatchesList.addAll(convertedMatches);
        filterMatchesByStatus(currentSelectedFilter);
    }

    /**
     * Ánh xạ các chuỗi trạng thái API sang các chuỗi trạng thái tiếng Việt có thể hiển thị.
     * @param apiStatus Chuỗi trạng thái từ API (ví dụ: "finished", "upcoming", "ongoing").
     * @return Chuỗi trạng thái tiếng Việt tương ứng.
     */
    private String mapApiStatusToDisplayStatus(String apiStatus) {
        switch (apiStatus) {
            case "finished":
                return "Đã kết thúc";
            case "upcoming":
                return "Sắp diễn ra";
            case "ongoing":
                return "Đang diễn ra";
            default:
                return apiStatus; // Trả về bản gốc nếu không tìm thấy ánh xạ
        }
    }

    private void filterMatchesByStatus(String statusFilter) {
        List<Matches> filteredList = new ArrayList<>();
        // Lọc danh sách hiện đã được chuyển đổi và hiển thị
        if (displayedMatchesList != null) {
            // Khi lọc, chúng ta nên áp dụng nó cho danh sách *vừa được tìm nạp/chuyển đổi*
            // cho `currentSelectedDateKey` và `currentViewType`.
            // Bản đồ `allMatchesData` lưu trữ các đối tượng `Match` API thô.
            // Chúng ta cần chuyển đổi lại và lọc từ đó.

            String compositeKey = currentSelectedDateKey + "_" + currentViewType; // Sử dụng currentViewType
            List<Match> rawMatchesForCurrentDateAndType = allMatchesData.get(compositeKey);

            if (rawMatchesForCurrentDateAndType != null) {
                // Chuyển đổi lại tất cả các trận đấu cho ngày và loại xem hiện tại để có một danh sách mới
                // trước khi áp dụng bộ lọc trạng thái.
                List<Matches> allConvertedMatchesForCurrentView = new ArrayList<>();
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                apiDateFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));

                for (Match rawMatch : rawMatchesForCurrentDateAndType) { // Lặp qua dữ liệu thô
                    String player1DisplayName = "";
                    String player2DisplayName = "";
                    String player1AvatarUrl1 = null;
                    String player1AvatarUrl2 = null;
                    String player2AvatarUrl1 = null;
                    String player2AvatarUrl2 = null;
                    String scoreDisplay = rawMatch.getTeam1Wins() + "-" + rawMatch.getTeam2Wins();
                    String matchTimeDisplay = "N/A";
                    String matchStatusDisplay = mapApiStatusToDisplayStatus(rawMatch.getStatus());

                    List<Participant> team1Participants = new ArrayList<>();
                    List<Participant> team2Participants = new ArrayList<>();

                    for (Participant p : rawMatch.getParticipants()) {
                        if (p.getTeam() == 1) {
                            team1Participants.add(p);
                        } else if (p.getTeam() == 2) {
                            team2Participants.add(p);
                        }
                    }

                    Collections.sort(team1Participants, Comparator.comparing(Participant::getFullName));
                    Collections.sort(team2Participants, Comparator.comparing(Participant::getFullName));

                    boolean isDoublesMatch = team1Participants.size() >= 2 || team2Participants.size() >= 2;

                    // Xử lý tên và avatar cho đội 1
                    if (!team1Participants.isEmpty()) {
                        player1DisplayName = team1Participants.get(0).getFullName();
                        player1AvatarUrl1 = team1Participants.get(0).getAvatarUrl();
                        if (team1Participants.size() >= 2) {
                            player1DisplayName += " & " + team1Participants.get(1).getFullName();
                            player1AvatarUrl2 = team1Participants.get(1).getAvatarUrl();
                        }
                        for (int i = 2; i < team1Participants.size(); i++) {
                            player1DisplayName += " & " + team1Participants.get(i).getFullName();
                        }
                    }

                    // Xử lý tên và avatar cho đội 2
                    if (!team2Participants.isEmpty()) {
                        player2DisplayName = team2Participants.get(0).getFullName();
                        player2AvatarUrl1 = team2Participants.get(0).getAvatarUrl();
                        if (team2Participants.size() >= 2) {
                            player2DisplayName += " & " + team2Participants.get(1).getFullName();
                            player2AvatarUrl2 = team2Participants.get(1).getAvatarUrl();
                        }
                        for (int i = 2; i < team2Participants.size(); i++) {
                            player2DisplayName += " & " + team2Participants.get(i).getFullName();
                        }
                    }

                    if (rawMatch.getStartTime() != null && !rawMatch.getStartTime().isEmpty()) {
                        try {
                            Date date = apiDateFormat.parse(rawMatch.getStartTime());
                            matchTimeDisplay = timeFormat.format(date);
                        } catch (java.text.ParseException e) {
                            Log.e("MatchesFragment", "Lỗi phân tích thời gian bắt đầu trận đấu để lọc: " + rawMatch.getStartTime(), e);
                        }
                    }
                    allConvertedMatchesForCurrentView.add(new Matches(player1DisplayName, player2DisplayName,
                            player1AvatarUrl1, player1AvatarUrl2,
                            player2AvatarUrl1, player2AvatarUrl2,
                            scoreDisplay, matchTimeDisplay, matchStatusDisplay,
                            isDoublesMatch));
                }

                // Bây giờ áp dụng bộ lọc trạng thái cho danh sách `allConvertedMatchesForCurrentView` mới này
                if (statusFilter.equals("Tất cả")) {
                    filteredList.addAll(allConvertedMatchesForCurrentView);
                } else {
                    for (Matches match : allConvertedMatchesForCurrentView) {
                        if (match.getMatchStatus().equals(statusFilter)) {
                            filteredList.add(match);
                        }
                    }
                }
            }
        }
        displayedMatchesList.clear();
        displayedMatchesList.addAll(filteredList);
        matchAdapter.notifyDataSetChanged();
    }
}