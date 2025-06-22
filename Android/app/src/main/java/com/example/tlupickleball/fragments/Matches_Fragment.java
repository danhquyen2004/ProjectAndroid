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
import android.widget.Toast;
import android.widget.FrameLayout;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;

public class Matches_Fragment extends Fragment implements DateAdapter.OnDateSelectedListener {

    private RecyclerView recyclerViewDates;
    private DateAdapter dateAdapter;
    private RecyclerView recyclerViewMatches;
    private MatchAdapter matchAdapter;
    private List<DateItem> datesList;
    private List<Matches> displayedMatchesList;
    private Map<String, List<Matches>> allMatchesData;

    private TextView textViewSelectedDate;
    private LinearLayout filterStatusContainer;
    private TextView textFilter;
    private FloatingActionButton btnAddMatch;

    private TextView item1, item2, select;
    private FrameLayout tabContentFrame;
    private String currentMatchType = "individual"; // Mặc định là "Cá nhân"

    private String currentSelectedDateKey;
    private String currentSelectedFilter = "Tất cả";

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
        selectedCal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.getDayOfMonth()));
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
        List<Matches> allMatchesForSelectedDate = new ArrayList<>();
        String compositeKey = dateKey + "_" + matchType;

        if (allMatchesData.containsKey(compositeKey)) {
            allMatchesForSelectedDate.addAll(allMatchesData.get(compositeKey));
            Log.d("MatchesFragment", "Found " + allMatchesForSelectedDate.size() + " matches for " + compositeKey + " from cache.");
        } else {
            List<Matches> matchesToLoad = createSampleMatches(dateKey, matchType);
            allMatchesData.put(compositeKey, matchesToLoad);
            allMatchesForSelectedDate.addAll(matchesToLoad);
            Log.d("MatchesFragment", "Generated " + matchesToLoad.size() + " sample matches for " + compositeKey);
        }

        displayedMatchesList.clear();
        displayedMatchesList.addAll(allMatchesForSelectedDate);
        filterMatchesByStatus(currentSelectedFilter);
    }

    /**
     * Tạo dữ liệu trận đấu mẫu dựa trên ngày và loại trận đấu (Cá nhân/Tổng quát).
     *
     * @param dateKey   Ngày ở định dạng "YYYY-MM-DD".
     * @param matchType Loại trận đấu ("individual" cho Cá nhân, "doubles" cho Tổng quát).
     * @return Danh sách các trận đấu mẫu.
     */
    private List<Matches> createSampleMatches(String dateKey, String matchType) {
        List<Matches> sampleMatches = new ArrayList<>();
        String statusDefault = "Sắp diễn ra"; // Trạng thái mặc định
        String myPlayerName = "Người chơi của tôi"; // Tên người chơi giả định

        // Sử dụng các avatar khác nhau để hiển thị đa dạng hơn
        int avatar1 = R.drawable.avatar_1; // Giả định là avatar của người chơi hiện tại
        int avatar2 = R.drawable.avatar_1;
        int avatar3 = R.drawable.avatar_1;
        int avatar4 = R.drawable.avatar_1;
        int avatar5 = R.drawable.avatar_1;
        int avatar6 = R.drawable.avatar_1;
        int avatar7 = R.drawable.avatar_1;
        int avatar8 = R.drawable.avatar_1;

        if (dateKey.contains("06-09")) { // Dữ liệu mẫu cho ngày 09/06
            if (matchType.equals("individual")) { // Kết quả Cá nhân (của 'Người chơi của tôi')
                // Trận đấu đơn có người chơi giả định
                sampleMatches.add(new Matches(myPlayerName, "Đối thủ A", avatar1, avatar2, "2-0", "18:00", "Đã kết thúc"));
                sampleMatches.add(new Matches("Đối thủ B", myPlayerName, avatar3, avatar1, "1-2", "17:00", "Đã kết thúc"));
                // Trận đấu đôi có người chơi giả định (tên đội bao gồm tên người chơi)
                sampleMatches.add(new Matches(myPlayerName + " & Đồng đội C", "Đối thủ D & Đối thủ E", avatar1, avatar4, "N/A", "19:00", "Sắp diễn ra"));
            } else { // Kết quả Tổng quát (của cả CLB)
                // Các trận đấu đơn chung
                sampleMatches.add(new Matches("Hoàng", "Lan", avatar5, avatar6, "N/A", "16:30", "Sắp diễn ra"));
                sampleMatches.add(new Matches("Minh", "Hà", avatar7, avatar8, "0-2", "14:00", "Đã kết thúc"));
                // Các trận đấu đôi chung
                sampleMatches.add(new Matches("Đội Thần Tốc", "Đội Bất Bại", avatar2, avatar3, "N/A", "20:00", "Đang diễn ra"));
                sampleMatches.add(new Matches("Song Sát", "Đôi Hùng Mạnh", avatar4, avatar5, "2-1", "15:00", "Đã kết thúc"));
            }
        } else if (dateKey.contains("06-10")) { // Dữ liệu mẫu cho ngày 10/06
            if (matchType.equals("individual")) { // Kết quả Cá nhân
                sampleMatches.add(new Matches(myPlayerName + " & Đồng đội F", "Đối thủ G & Đối thủ H", avatar1, avatar6, "N/A", "09:00", "Sắp diễn ra"));
            } else { // Kết quả Tổng quát
                sampleMatches.add(new Matches("Team Phượng Hoàng", "Team Rồng Xanh", avatar7, avatar8, "N/A", "10:00", "Sắp diễn ra"));
                sampleMatches.add(new Matches("Long", "Mai", avatar1, avatar2, "N/A", "11:00", "Sắp diễn ra")); // Ví dụ trận đấu đơn trong Tổng quát
            }
        } else {
            // Dữ liệu mặc định nếu không có dữ liệu cụ thể cho ngày và loại được chọn
            if (matchType.equals("individual")) {
                sampleMatches.add(new Matches(myPlayerName, "Đối thủ Mặc định (Cá nhân)", avatar1, avatar2, "N/A", "N/A", statusDefault));
            } else {
                sampleMatches.add(new Matches("Tổng quát Team Default 1", "Tổng quát Team Default 2", avatar3, avatar4, "N/A", "N/A", statusDefault));
            }
        }
        return sampleMatches;
    }


    private void filterMatchesByStatus(String statusFilter) {
        List<Matches> filteredList = new ArrayList<>();
        String compositeKey = currentSelectedDateKey + "_" + currentMatchType;
        List<Matches> matchesForCurrentDateAndType = allMatchesData.get(compositeKey);

        if (matchesForCurrentDateAndType != null) {
            if (statusFilter.equals("Tất cả")) {
                filteredList.addAll(matchesForCurrentDateAndType);
            } else {
                for (Matches match : matchesForCurrentDateAndType) {
                    if (match.getMatchStatus().equals(statusFilter)) {
                        filteredList.add(match);
                    }
                }
            }
        }
        displayedMatchesList.clear();
        displayedMatchesList.addAll(filteredList);
        matchAdapter.notifyDataSetChanged();
    }
}