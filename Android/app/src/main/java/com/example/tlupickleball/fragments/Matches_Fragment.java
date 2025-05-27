package com.example.tlupickleball.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;

import com.example.tlupickleball.R;
import com.example.tlupickleball.adapters.DateAdapter;
import com.example.tlupickleball.adapters.MatchAdapter;
import com.example.tlupickleball.model.DateItem;
import com.example.tlupickleball.model.Matches;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Matches_Fragment extends Fragment implements DateAdapter.OnDateClickListener {

    private ImageView imgHeader;
    private TextView tvTabCaNhan;
    private TextView tvTabTongQuat;
    private TextView tvTabIndicator;

    private RecyclerView recyclerViewDates;
    private RecyclerView recyclerViewMatches;

    private DateAdapter dateAdapter;
    private MatchAdapter matchAdapter;

    private List<DateItem> dateList = new ArrayList<>();
    private List<Matches> allMatchesList = new ArrayList<>();

    private String currentSelectedDateFilter = null;
    private String currentSelectedTabCriteria = "Cá nhân";

    public Matches_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_matches, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgHeader = view.findViewById(R.id.imgHeader);

        View customTabLayoutView = view.findViewById(R.id.customTabLayout);
        if (customTabLayoutView != null) {
            View tabContentFrame = customTabLayoutView.findViewById(R.id.tab_content_frame);
            if (tabContentFrame != null) {
                tvTabCaNhan = tabContentFrame.findViewById(R.id.item1);
                tvTabTongQuat = tabContentFrame.findViewById(R.id.item2);
                tvTabIndicator = tabContentFrame.findViewById(R.id.select);
            } else {
                tvTabCaNhan = customTabLayoutView.findViewById(R.id.item1);
                tvTabTongQuat = customTabLayoutView.findViewById(R.id.item2);
                tvTabIndicator = customTabLayoutView.findViewById(R.id.select);
            }
        }

        recyclerViewDates = view.findViewById(R.id.recyclerViewDates);
        recyclerViewMatches = view.findViewById(R.id.recyclerViewMatches);

        recyclerViewDates.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        dateAdapter = new DateAdapter(getContext(), dateList, this);
        recyclerViewDates.setAdapter(dateAdapter);

        recyclerViewMatches.setLayoutManager(new LinearLayoutManager(getContext()));
        matchAdapter = new MatchAdapter(getContext(), new ArrayList<>());
        recyclerViewMatches.setAdapter(matchAdapter);

        prepareDateData();
        prepareMatchData();

        setupCustomTabs();

        if (!dateList.isEmpty()) {
            String todayFormatted = getTodayFormattedForFilter();
            int todayPosition = findDateItemPosition(todayFormatted);
            if (todayPosition != -1) {
                dateAdapter.setSelectedPosition(todayPosition);
                onDateClick(dateList.get(todayPosition), todayPosition);
            } else {
                dateAdapter.setSelectedPosition(0);
                onDateClick(dateList.get(0), 0);
            }
        }
    }

    private void setupCustomTabs() {
        if (tvTabCaNhan != null && tvTabTongQuat != null && tvTabIndicator != null) {
            selectCustomTab(tvTabCaNhan);
            handleTabSelection("Cá nhân"); // Kích hoạt bộ lọc mặc định

            tvTabCaNhan.setOnClickListener(v -> {
                selectCustomTab(tvTabCaNhan);
                handleTabSelection("Cá nhân");
            });

            tvTabTongQuat.setOnClickListener(v -> {
                selectCustomTab(tvTabTongQuat);
                handleTabSelection("Tổng quát");
            });
        } else {
            if (getContext() != null) {
                Toast.makeText(getContext(), "Lỗi: Không tìm thấy các tab tùy chỉnh", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void selectCustomTab(TextView selectedTab) {
        if (getContext() == null) return;

        if (tvTabCaNhan != null) {
            tvTabCaNhan.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
        }
        if (tvTabTongQuat != null) {
            tvTabTongQuat.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
        }

        selectedTab.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));

        selectedTab.post(() -> {
            animateTabIndicator(selectedTab);
        });
    }

    private void animateTabIndicator(TextView selectedTab) {
        if (tvTabIndicator == null) return;

        int targetX = selectedTab.getLeft();
        int targetWidth = selectedTab.getWidth();

        ViewGroup.LayoutParams params = tvTabIndicator.getLayoutParams();
        if (params instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) params;
            lp.width = targetWidth;
            tvTabIndicator.setLayoutParams(lp);
        }

        tvTabIndicator.animate()
                .translationX(targetX)
                .setDuration(200)
                .start();
    }

    private void prepareDateData() {
        dateList.clear();
        LocalDate today = LocalDate.now();

        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("M");
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("d");

        for (int i = -1; i <= 5; i++) {
            LocalDate date = today.plusDays(i);
            String day = dayFormatter.format(date);
            String month = "tháng " + monthFormatter.format(date);
            dateList.add(new DateItem(day, month));
        }
        dateAdapter.notifyDataSetChanged();
    }

    private void prepareMatchData() {
        allMatchesList.clear();

        // Giả lập dữ liệu trận đấu
        // Ngày 25/05
        allMatchesList.add(new Matches("Tùng", R.drawable.avatar_1, "Nam", R.drawable.avatar_1, "2 - 0", "25 tháng 5", "Giải đấu 1", true, "25/5"));
        allMatchesList.add(new Matches("Linh", R.drawable.avatar_1, "Hải", R.drawable.avatar_1, "1 - 2", "25 tháng 5", "Giải đấu 2", false, "25/5"));
        allMatchesList.add(new Matches("Minh", R.drawable.avatar_1, "Hoa", R.drawable.avatar_1, "2 - 1", "25 tháng 5", "Giải đấu 1", false, "25/5"));

        // Ngày 26/05
        allMatchesList.add(new Matches("Phong", R.drawable.avatar_1, "Lan", R.drawable.avatar_1, "0 - 2", "26 tháng 5", "Giải đấu 3", true, "26/5"));
        allMatchesList.add(new Matches("Thảo", R.drawable.avatar_1, "Sơn", R.drawable.avatar_1, "2 - 0", "26 tháng 5", "Giải đấu 2", false, "26/5"));

        // Ngày 27/05
        allMatchesList.add(new Matches("Trang", R.drawable.avatar_1, "Long", R.drawable.avatar_1, "1 - 1", "27 tháng 5", "Giải đấu 1", true, "27/5"));
        allMatchesList.add(new Matches("Quang", R.drawable.avatar_1, "Uyên", R.drawable.avatar_1, "2 - 0", "27 tháng 5", "Giải đấu 3", false, "27/5"));

        // Ngày 28/05 (Ví dụ hôm nay)
        allMatchesList.add(new Matches("Việt", R.drawable.avatar_1, "Nga", R.drawable.avatar_1, "2 - 1", "28 tháng 5", "Giải đấu 2", true, "28/5"));
        allMatchesList.add(new Matches("Hùng", R.drawable.avatar_1, "Thu", R.drawable.avatar_1, "0 - 2", "28 tháng 5", "Giải đấu 1", false, "28/5"));
        allMatchesList.add(new Matches("Duy", R.drawable.avatar_1, "Mai", R.drawable.avatar_1, "2 - 0", "28 tháng 5", "Giải đấu 3", false, "28/5"));

        matchAdapter.updateMatches(allMatchesList);
    }

    private String getTodayFormattedForFilter() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M", Locale.getDefault());
        return formatter.format(today);
    }

    private int findDateItemPosition(String dateFilter) {
        for (int i = 0; i < dateList.size(); i++) {
            DateItem item = dateList.get(i);
            String itemMonthNum = item.getMonth().replace("tháng ", "").trim();
            String itemFormatted = item.getDay() + "/" + itemMonthNum;
            if (itemFormatted.equals(dateFilter)) {
                return i;
            }
        }
        return -1;
    }

    private void handleTabSelection(String tabText) {
        currentSelectedTabCriteria = tabText;
        // Không cần ẩn/hiện recyclerViewDates và recyclerViewMatches ở đây nữa
        // vì chúng luôn hiển thị. Logic hiển thị được điều khiển bởi applyFilters.

        if (getContext() != null) {
            Toast.makeText(getContext(), "Đang hiển thị " + tabText + " trận đấu", Toast.LENGTH_SHORT).show();
        }
        applyFilters(); // Luôn gọi applyFilters để cập nhật RecyclerView
    }

    @Override
    public void onDateClick(DateItem dateModel, int position) {
        if (getContext() != null) {
            Toast.makeText(getContext(), "Chọn ngày: " + dateModel.getDay() + " " + dateModel.getMonth(), Toast.LENGTH_SHORT).show();
        }
        String monthNumber = dateModel.getMonth().replace("tháng ", "").trim();
        currentSelectedDateFilter = dateModel.getDay() + "/" + monthNumber;
        applyFilters();
    }

    private void applyFilters() {
        if (matchAdapter == null) return;

        // Cả hai RecyclerView (date và matches) đều sẽ luôn hiển thị.
        // Chỉ cần gọi filterMatches với các tiêu chí hiện tại.
        matchAdapter.filterMatches(currentSelectedDateFilter, currentSelectedTabCriteria);
    }
}