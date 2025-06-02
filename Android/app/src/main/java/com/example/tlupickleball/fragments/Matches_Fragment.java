package com.example.tlupickleball.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlupickleball.R;
import com.example.tlupickleball.adapters.DateAdapter;
import com.example.tlupickleball.adapters.MatchAdapter;
import com.example.tlupickleball.model.DateItem;
import com.example.tlupickleball.model.Matches;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap; // Thêm import này
import java.util.List;
import java.util.Locale;
import java.util.Map; // Thêm import này

import android.app.AlertDialog;
import android.content.DialogInterface;

public class Matches_Fragment extends Fragment implements DateAdapter.OnDateSelectedListener {

    private RecyclerView recyclerViewDates;
    private RecyclerView recyclerViewMatches;
    private TextView textViewSelectedDate;
    private TextView textFilter;
    private LinearLayout filterButton;
    private FloatingActionButton btnAddMatch;
    private DateAdapter dateAdapter;
    private MatchAdapter matchAdapter;
    private List<DateItem> datesList;
    private List<Matches> allMatchesForSelectedDate;
    private List<Matches> displayedMatchesList;

    private TextView item1Tab;
    private TextView item2Tab;
    private TextView selectTabIndicator;

    private String currentMatchType = "individual";
    private String currentSelectedFilter = "Tất cả";
    private DateItem currentlySelectedDate; // Sẽ lưu trữ ngày hiện tại được chọn

    // Maps để lưu trữ dữ liệu giả động
    private Map<String, List<Matches>> dummyIndividualMatchesData;
    private Map<String, List<Matches>> dummyGeneralMatchesData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_matches, container, false);

        recyclerViewDates = view.findViewById(R.id.recyclerViewDates);
        recyclerViewMatches = view.findViewById(R.id.recyclerViewMatches);
        textViewSelectedDate = view.findViewById(R.id.textViewSelectedDate);
        textFilter = view.findViewById(R.id.textFilter);
        filterButton = view.findViewById(R.id.filterButton);
        btnAddMatch = view.findViewById(R.id.btnAddMatch);

        item1Tab = view.findViewById(R.id.item1);
        item2Tab = view.findViewById(R.id.item2);
        selectTabIndicator = view.findViewById(R.id.select);

        // Khởi tạo dữ liệu giả ngay từ đầu
        initializeDummyData();

        setupCustomTabs();
        setupMatchRecyclerView();
        setupFilterButton();
        setupAddMatchButton();

        // --- Bắt đầu logic khởi tạo ngày tháng và tải dữ liệu mặc định ---
        setupDateRecyclerView(); // Tạo datesList và khởi tạo Adapter (ban đầu với width=0)

        // Sau khi datesList được tạo và layout đã sẵn sàng, tính toán chiều rộng và cập nhật Adapter
        recyclerViewDates.post(() -> {
            int width = recyclerViewDates.getWidth();
            if (width > 0) {
                // Khởi tạo lại dateAdapter với chiều rộng thực tế
                // và truyền ngày đầu tiên là ngày được chọn mặc định
                if (!datesList.isEmpty()) {
                    currentlySelectedDate = datesList.get(0);
                    currentlySelectedDate.setSelected(true); // Đánh dấu ngày đầu tiên là được chọn
                }

                dateAdapter = new DateAdapter(datesList, this, width);
                recyclerViewDates.setAdapter(dateAdapter);

                // Cập nhật text hiển thị ngày đã chọn (là ngày hiện tại)
                if (currentlySelectedDate != null) {
                    updateSelectedDateText(currentlySelectedDate);
                }

                // Thực hiện click giả lập vào tab "Cá nhân" để tải dữ liệu mặc định
                // (Sau khi đã đảm bảo selectTabIndicator có vị trí và kích thước chính xác)
                item1Tab.post(() -> {
                    ViewGroup.LayoutParams params = selectTabIndicator.getLayoutParams();
                    params.width = item1Tab.getWidth(); // Set width to match tab
                    selectTabIndicator.setLayoutParams(params);
                    selectTabIndicator.setX(item1Tab.getX()); // Set initial position
                    item1Tab.performClick(); // Kích hoạt tab "Cá nhân" và tải dữ liệu
                });

            } else {
                Log.e("Matches_Fragment", "recyclerViewDates width is 0. Cannot setup DateAdapter for even distribution.");
                // Fallback nếu không thể lấy chiều rộng, vẫn cố gắng tải dữ liệu
                if (currentlySelectedDate != null) {
                    loadMatchesForDate(currentlySelectedDate, currentMatchType);
                }
            }
        });
        // --- Kết thúc logic khởi tạo ngày tháng và tải dữ liệu mặc định ---

        return view;
    }

    private void setupCustomTabs() {
        item1Tab.setOnClickListener(v -> {
            currentMatchType = "individual";
            selectTabIndicator.animate().x(0).setDuration(300);
            item1Tab.setTextColor(Color.WHITE);
            item2Tab.setTextColor(Color.BLACK);
            item1Tab.setBackgroundResource(android.R.color.transparent);
            item2Tab.setBackgroundResource(android.R.color.transparent);

            // Tải dữ liệu cho tab "Cá nhân" cho ngày hiện tại
            if (currentlySelectedDate != null) {
                loadMatchesForDate(currentlySelectedDate, currentMatchType);
            }
        });

        item2Tab.setOnClickListener(v -> {
            currentMatchType = "general";
            selectTabIndicator.animate().x(item2Tab.getX()).setDuration(300);
            item1Tab.setTextColor(Color.BLACK);
            item2Tab.setTextColor(Color.WHITE);
            item1Tab.setBackgroundResource(android.R.color.transparent);
            item2Tab.setBackgroundResource(android.R.color.transparent);

            // Tải dữ liệu cho tab "Tổng quát" cho ngày hiện tại
            if (currentlySelectedDate != null) {
                loadMatchesForDate(currentlySelectedDate, currentMatchType);
            }
        });
    }

    private void setupDateRecyclerView() {
        datesList = generateDatesForNext7Days();
        // Khởi tạo dateAdapter ban đầu với chiều rộng 0, sẽ được cập nhật sau
        dateAdapter = new DateAdapter(datesList, this, 0); // <-- Truyền 0 width ban đầu
        recyclerViewDates.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewDates.setAdapter(dateAdapter);

        // Không cần gọi notifyItemChanged(0) ở đây, vì logic post() sẽ xử lý
    }

    private void setupMatchRecyclerView() {
        allMatchesForSelectedDate = new ArrayList<>();
        displayedMatchesList = new ArrayList<>();
        matchAdapter = new MatchAdapter(displayedMatchesList);
        recyclerViewMatches.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewMatches.setAdapter(matchAdapter);
    }

    private void setupFilterButton() {
        textFilter.setText("Tất cả");
        filterButton.setOnClickListener(v -> {
            showFilterOptionsDialog();
        });
    }

    private void showFilterOptionsDialog() {
        final String[] filterOptions = {"Tất cả", "Đang diễn ra", "Sắp diễn ra", "Đã kết thúc"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Lọc trận đấu theo trạng thái");
        builder.setItems(filterOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentSelectedFilter = filterOptions[which];
                textFilter.setText(currentSelectedFilter);
                filterMatchesByStatus(currentSelectedFilter);
            }
        });
        builder.show();
    }

    private void setupAddMatchButton() {
        btnAddMatch.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Nút Thêm trận đấu đã được nhấp!", Toast.LENGTH_SHORT).show();
            // Điều hướng đến màn hình "Thêm trận đấu" hoặc hiển thị một dialog
        });
    }

    private List<DateItem> generateDatesForNext7Days() {
        List<DateItem> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        Locale locale = new Locale("vi", "VN");
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
        // Sử dụng "MMMM" để lấy tên tháng đầy đủ (ví dụ: "Tháng 6")
        SimpleDateFormat monthDisplayFormat = new SimpleDateFormat("MMMM", locale);

        for (int i = 0; i < 7; i++) {
            String day = dayFormat.format(calendar.getTime());
            String monthDisplay = monthDisplayFormat.format(calendar.getTime()); // Tháng để hiển thị

            dates.add(new DateItem(day, monthDisplay));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return dates;
    }

    private void updateSelectedDateText(DateItem date) {
        String formattedDate = String.format(Locale.getDefault(), "Ngày %s %s", date.getDayOfMonth(), date.getMonth());
        textViewSelectedDate.setText(formattedDate);
    }

    @Override
    public void onDateSelected(DateItem date) {
        currentlySelectedDate = date;
        updateSelectedDateText(date);
        loadMatchesForDate(date, currentMatchType); // Tải lại trận đấu khi ngày được chọn
        Toast.makeText(getContext(), "Ngày được chọn: " + date.getDayOfMonth() + " " + date.getMonth(), Toast.LENGTH_SHORT).show();
    }

    // Phương thức để khởi tạo dữ liệu giả động
    private void initializeDummyData() {
        dummyIndividualMatchesData = new HashMap<>();
        dummyGeneralMatchesData = new HashMap<>();

        Calendar calendar = Calendar.getInstance(); // Lấy ngày hiện tại
        Locale locale = new Locale("vi", "VN");
        // Định dạng "dd MMMM" để tạo key ví dụ: "03 Tháng 6"
        SimpleDateFormat dayMonthFormat = new SimpleDateFormat("dd MMMM", locale);

        // Tạo dữ liệu giả cho ngày hôm nay và 6 ngày tiếp theo
        for (int i = 0; i < 7; i++) {
            String dateKey = dayMonthFormat.format(calendar.getTime()); // Ví dụ: "03 Tháng 6"
            Log.d("DummyData", "Generating dummy data for: " + dateKey);

            List<Matches> individualList = new ArrayList<>();
            List<Matches> generalList = new ArrayList<>();

            // Điền dữ liệu cá nhân
            if (i == 0) { // Dữ liệu cho Ngày Hôm Nay
                individualList.add(new Matches("Nguyễn An (CN)", "Trần Bình (CN)", R.drawable.avatar_1, R.drawable.avatar_1, "2 - 1", "09h00", "Đã kết thúc"));
                individualList.add(new Matches("Lê Cường (CN)", "Phạm Dung (CN)", R.drawable.avatar_1, R.drawable.avatar_1, "0 - 0", "11h30", "Sắp diễn ra"));
                individualList.add(new Matches("Hoàng Giáp (CN)", "Vũ Hùng (CN)", R.drawable.avatar_1, R.drawable.avatar_1, "1 - 1", "15h00", "Đang diễn ra"));
            } else if (i == 1) { // Dữ liệu cho Ngày Mai
                individualList.add(new Matches("Đinh Kha (CN)", "Mai Lan (CN)", R.drawable.avatar_1, R.drawable.avatar_1, "0 - 0", "10h00", "Sắp diễn ra"));
                individualList.add(new Matches("Tôn Nam (CN)", "Cao Phong (CN)", R.drawable.avatar_1, R.drawable.avatar_1, "2 - 0", "14h00", "Đang diễn ra"));
            } else if (i == 2) { // Dữ liệu cho Ngày Kia
                individualList.add(new Matches("Dương Quyết (CN)", "Bùi Sáng (CN)", R.drawable.avatar_1, R.drawable.avatar_1, "1 - 2", "08h30", "Đã kết thúc"));
            }
            // Các ngày còn lại (i=3 đến 6) sẽ không có dữ liệu cụ thể, và sẽ hiển thị dữ liệu mặc định

            // Điền dữ liệu tổng quát
            if (i == 0) { // Dữ liệu cho Ngày Hôm Nay
                generalList.add(new Matches("Đội Vàng (TQ)", "Đội Xanh (TQ)", R.drawable.avatar_1, R.drawable.avatar_1, "3 - 2", "09h30", "Đã kết thúc"));
                generalList.add(new Matches("Đội Đỏ (TQ)", "Đội Trắng (TQ)", R.drawable.avatar_1, R.drawable.avatar_1, "0 - 0", "13h00", "Đang diễn ra"));
                generalList.add(new Matches("Đội Đen (TQ)", "Đội Tím (TQ)", R.drawable.avatar_1, R.drawable.avatar_1, "0 - 0", "19h00", "Sắp diễn ra"));
            } else if (i == 1) { // Dữ liệu cho Ngày Mai
                generalList.add(new Matches("Đội Sói (TQ)", "Đội Gấu (TQ)", R.drawable.avatar_1, R.drawable.avatar_1, "1 - 0", "16h00", "Đang diễn ra"));
            } else if (i == 2) { // Dữ liệu cho Ngày Kia
                generalList.add(new Matches("Đội Hổ (TQ)", "Đội Báo (TQ)", R.drawable.avatar_1, R.drawable.avatar_1, "0 - 0", "10h30", "Sắp diễn ra"));
                generalList.add(new Matches("Đội Chim (TQ)", "Đội Cá (TQ)", R.drawable.avatar_1, R.drawable.avatar_1, "2 - 2", "14h30", "Đã kết thúc"));
            }
            // Các ngày còn lại (i=3 đến 6) sẽ không có dữ liệu cụ thể, và sẽ hiển thị dữ liệu mặc định

            dummyIndividualMatchesData.put(dateKey, individualList);
            dummyGeneralMatchesData.put(dateKey, generalList);

            calendar.add(Calendar.DAY_OF_MONTH, 1); // Chuyển sang ngày tiếp theo
        }
    }


    private void loadMatchesForDate(DateItem date, String matchType) {
        allMatchesForSelectedDate.clear();

        String selectedDay = date.getDayOfMonth();
        String selectedMonthDisplay = date.getMonth(); // Bây giờ sẽ là "Tháng 6"
        // Tạo key để tìm kiếm trong map dữ liệu giả
        String dateKey = selectedDay + " " + selectedMonthDisplay;

        Log.d("MatchesFragment", "Loading matches for key: " + dateKey + ", Match Type: " + matchType);

        List<Matches> matchesToLoad = null;

        if (matchType.equals("individual")) {
            matchesToLoad = dummyIndividualMatchesData.get(dateKey);
        } else { // general matches
            matchesToLoad = dummyGeneralMatchesData.get(dateKey);
        }

        if (matchesToLoad != null && !matchesToLoad.isEmpty()) {
            allMatchesForSelectedDate.addAll(matchesToLoad);
            Log.d("MatchesFragment", "Found " + matchesToLoad.size() + " matches for " + dateKey);
        } else {
            // Dữ liệu mặc định nếu không có dữ liệu cụ thể cho ngày được chọn
            String defaultPlayer1 = (matchType.equals("individual") ? "Cá nhân" : "Tổng quát") + " mặc định";
            String defaultPlayer2 = "Đối thủ mặc định";
            String defaultStatus = "Sắp diễn ra"; // Chọn trạng thái mặc định phù hợp
            allMatchesForSelectedDate.add(new Matches(defaultPlayer1 + " (" + selectedDay + " " + selectedMonthDisplay + ")", defaultPlayer2, R.drawable.avatar_1, R.drawable.avatar_1, "N/A", "N/A", defaultStatus));
            Log.d("MatchesFragment", "No specific data for " + dateKey + ", added default match.");
        }
        filterMatchesByStatus(currentSelectedFilter); // Sau khi tải, lọc theo trạng thái hiện tại
    }

    private void filterMatchesByStatus(String statusFilter) {
        displayedMatchesList.clear();
        if (statusFilter.equals("Tất cả")) {
            displayedMatchesList.addAll(allMatchesForSelectedDate);
        } else {
            for (Matches match : allMatchesForSelectedDate) {
                if (match.getMatchStatus().equals(statusFilter)) {
                    displayedMatchesList.add(match);
                }
            }
        }
        matchAdapter.notifyDataSetChanged(); // Rất quan trọng để cập nhật UI
    }
}