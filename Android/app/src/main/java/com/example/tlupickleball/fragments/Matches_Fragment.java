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
    // Changed allMatchesData to store network.model.Match to cache raw API data
    private Map<String, List<Match>> allMatchesData;

    private TextView textViewSelectedDate;
    private LinearLayout filterStatusContainer;
    private TextView textFilter;
    private FloatingActionButton btnAddMatch;

    private TextView item1, item2, select;
    private FrameLayout tabContentFrame;
    private String currentMatchType = "individual"; // Mặc định là "Cá nhân"

    private String currentSelectedDateKey;
    private String currentSelectedFilter = "Tất cả";

    // Add MatchService instance
    private MatchService matchService;

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

        // Initialize MatchService using ApiClient
        matchService = ApiClient.getClient(getContext()).create(MatchService.class);

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
        updateTabSelection(currentMatchType);
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
            item1.setOnClickListener(v -> {
                if (!currentMatchType.equals("individual")) {
                    currentMatchType = "individual";
                    updateTabSelection(currentMatchType);
                    if (currentSelectedDateKey != null) {
                        loadMatchesForSelectedDate(currentSelectedDateKey, currentMatchType);
                    }
                }
            });
        }

        if (item2 != null) {
            item2.setOnClickListener(v -> {
                if (!currentMatchType.equals("doubles")) {
                    currentMatchType = "doubles";
                    updateTabSelection(currentMatchType);
                    if (currentSelectedDateKey != null) {
                        loadMatchesForSelectedDate(currentSelectedDateKey, currentMatchType);
                    }
                }
            });
        }

        updateTabSelection(currentMatchType);
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
        } else if (selectedType.equals("doubles")) {
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
            selectedCal.set(Calendar.YEAR, now.get(Calendar.YEAR)); // Explicitly set the current year
        } catch (java.text.ParseException e) {
            Log.e("MatchesFragment", "Error parsing month for date selection: " + e.getMessage());
            selectedCal = Calendar.getInstance(); // Fallback to today's date
        }

        SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        currentSelectedDateKey = fullDateFormat.format(selectedCal.getTime());

        loadMatchesForSelectedDate(currentSelectedDateKey, currentMatchType);
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

    private void loadMatchesForSelectedDate(String dateKey, String matchType) {
        String compositeKey = dateKey + "_" + matchType;

        if (allMatchesData.containsKey(compositeKey)) {
            // Data is in cache, convert and display
            List<Match> cachedMatches = allMatchesData.get(compositeKey);
            Log.d("MatchesFragment", "Found " + cachedMatches.size() + " matches for " + compositeKey + " from cache.");
            convertAndDisplayMatches(cachedMatches, matchType);
        } else {
            // Fetch data from API
            Log.d("MatchesFragment", "Fetching matches for " + compositeKey + " from API.");
            int page = 1; // Assuming default page 1
            int pageSize = 50; // Assuming default page size 50

            matchService.getMatchesByDay(dateKey, page, pageSize).enqueue(new Callback<MatchResponse>() {
                @Override
                public void onResponse(Call<MatchResponse> call, Response<MatchResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Match> fetchedMatches = response.body().getMatches();
                        allMatchesData.put(compositeKey, fetchedMatches); // Cache the raw API data
                        convertAndDisplayMatches(fetchedMatches, matchType);
                        Log.d("MatchesFragment", "Fetched " + fetchedMatches.size() + " matches for " + compositeKey + " from API.");
                    } else {
                        Log.e("MatchesFragment", "API call failed: " + response.message());
                        Toast.makeText(getContext(), "Failed to load matches: " + response.message(), Toast.LENGTH_SHORT).show();
                        displayedMatchesList.clear(); // Clear displayed matches if API call fails
                        matchAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<MatchResponse> call, Throwable t) {
                    Log.e("MatchesFragment", "API call error: " + t.getMessage(), t);
                    Toast.makeText(getContext(), "Error loading matches: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    displayedMatchesList.clear(); // Clear displayed matches if API call fails
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

            List<Participant> team1Participants = new ArrayList<>();
            List<Participant> team2Participants = new ArrayList<>();

            for (Participant p : rawMatch.getParticipants()) {
                if (p.getTeam() == 1) {
                    team1Participants.add(p);
                } else if (p.getTeam() == 2) {
                    team2Participants.add(p);
                }
            }

            // Sort participants by full name to ensure consistent order for team display names
            Collections.sort(team1Participants, Comparator.comparing(Participant::getFullName));
            Collections.sort(team2Participants, Comparator.comparing(Participant::getFullName));

            if (matchType.equals("individual")) {
                // Assuming individual matches have one participant per team
                if (!team1Participants.isEmpty()) {
                    player1DisplayName = team1Participants.get(0).getFullName();
                }
                if (!team2Participants.isEmpty()) {
                    player2DisplayName = team2Participants.get(0).getFullName();
                }
            } else if (matchType.equals("doubles")) {
                // Assuming doubles matches have two participants per team or more
                List<String> team1Names = new ArrayList<>();
                for (Participant p : team1Participants) {
                    team1Names.add(p.getFullName());
                }
                player1DisplayName = String.join(" & ", team1Names);

                List<String> team2Names = new ArrayList<>();
                for (Participant p : team2Participants) {
                    team2Names.add(p.getFullName());
                }
                player2DisplayName = String.join(" & ", team2Names);
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

            // Using avatar_1 as placeholder since actual avatar URLs are not in the API response
            convertedMatches.add(new Matches(player1DisplayName, player2DisplayName,
                    R.drawable.avatar_1, R.drawable.avatar_1,
                    scoreDisplay, matchTimeDisplay, matchStatusDisplay));
        }

        displayedMatchesList.clear();
        displayedMatchesList.addAll(convertedMatches);
        filterMatchesByStatus(currentSelectedFilter); // Re-apply filter after data conversion
    }

    /**
     * Maps API status strings to displayable Vietnamese status strings.
     * @param apiStatus The status string from the API (e.g., "finished", "upcoming", "ongoing").
     * @return The corresponding Vietnamese status string.
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
                return apiStatus; // Return original if no mapping found
        }
    }

    private void filterMatchesByStatus(String statusFilter) {
        List<Matches> filteredList = new ArrayList<>();
        // Filter the currently converted and displayed list
        if (displayedMatchesList != null) {
            if (statusFilter.equals("Tất cả")) {
                // To get all matches for current date and type, we need to re-convert from the cached raw data
                // This ensures filtering always starts from the full set of fetched matches
                String compositeKey = currentSelectedDateKey + "_" + currentMatchType;
                List<Match> rawMatchesForCurrentDateAndType = allMatchesData.get(compositeKey);
                if (rawMatchesForCurrentDateAndType != null) {
                    // Re-convert all matches and then filter (or just add all if filter is "Tất cả")
                    // This path means we will re-run convertAndDisplayMatches, which then calls this filter again,
                    // this would be slightly inefficient. A better way is to cache the *converted* list
                    // or to filter the `displayedMatchesList` itself based on the raw `allMatchesData`.
                    // For simplicity and correctness with current logic, let's just re-add all from `displayedMatchesList`
                    // if it's already updated, or update `displayedMatchesList` from `allMatchesData` first.
                    // Given `convertAndDisplayMatches` already populates `displayedMatchesList` before calling this,
                    // `displayedMatchesList` holds all matches for the current date/type.
                    filteredList.addAll(displayedMatchesList);
                }
            } else {
                // Iterate through the already converted and potentially filtered list
                String compositeKey = currentSelectedDateKey + "_" + currentMatchType;
                List<Match> rawMatchesForCurrentDateAndType = allMatchesData.get(compositeKey);
                if (rawMatchesForCurrentDateAndType != null) {
                    List<Matches> allConvertedMatchesForFiltering = new ArrayList<>();
                    // Re-convert all to apply filter accurately if needed, or filter directly on `rawMatchesForCurrentDateAndType`
                    // and then convert.
                    // To avoid reconversion, let's assume `displayedMatchesList` currently holds the complete list
                    // before the *last* filter was applied. If it's not, we'd need a separate `fullMatchesListForDisplay`
                    // that always contains all converted matches for the current date/type.
                    // For current structure, `displayedMatchesList` is set in `convertAndDisplayMatches`
                    // to all matches of current date/type before this filter.
                    for (Matches match : displayedMatchesList) { // This `displayedMatchesList` has *all* matches from API for this date/type (if `convertAndDisplayMatches` just ran).
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