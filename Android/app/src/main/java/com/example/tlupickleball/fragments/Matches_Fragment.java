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
import com.example.tlupickleball.network.core.SessionManager; // Import SessionManager
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
    private Map<String, List<Match>> allMatchesData; // Cache cho dữ liệu API thô

    private TextView textViewSelectedDate;
    private LinearLayout filterStatusContainer;
    private TextView textFilter;
    private FloatingActionButton btnAddMatch;

    private TextView item1, item2, select; // Các phần tử cho tab layout
    private FrameLayout tabContentFrame; // Frame chứa các tab

    private String currentMatchType = "club_all"; // Mặc định là "Tổng quát cả clb"
    private String currentSelectedDateKey; // Ngày hiện tại được chọn (định dạng yyyy-MM-dd)
    private String currentSelectedFilter = "Tất cả"; // Bộ lọc trạng thái trận đấu hiện tại

    // Khai báo instance của MatchService và userId của người dùng hiện tại
    private MatchService matchService;
    private String currentUserId; // Để lưu trữ User ID của người dùng hiện tại

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_matches, container, false);

        // Ánh xạ các View
        recyclerViewDates = root.findViewById(R.id.recyclerViewDates);
        recyclerViewMatches = root.findViewById(R.id.recyclerViewMatches);
        textViewSelectedDate = root.findViewById(R.id.textViewSelectedDate);
        filterStatusContainer = root.findViewById(R.id.filterButton);
        textFilter = root.findViewById(R.id.textFilter);
        btnAddMatch = root.findViewById(R.id.btnAddMatch);

        item1 = root.findViewById(R.id.item1); // Tab "Tổng quát cả clb"
        item2 = root.findViewById(R.id.item2); // Tab "Cá nhân"
        select = root.findViewById(R.id.select); // Thanh chọn tab
        tabContentFrame = root.findViewById(R.id.tab_content_frame); // Frame cha của các tab

        // Khởi tạo MatchService và lấy User ID của người dùng hiện tại
        matchService = ApiClient.getClient(getContext()).create(MatchService.class);
        // GIẢ ĐỊNH: Bạn đã có một lớp SessionManager để lấy User ID.
        // Ví dụ: SessionManager.getUserId(getContext())
        // Bạn cần đảm bảo SessionManager này tồn tại và trả về userId chính xác.
        currentUserId = SessionManager.getUid(getContext());
        if (currentUserId == null || currentUserId.isEmpty()) {
            Log.e("MatchesFragment", "User ID is null or empty. Personal matches might not load.");
            // Có thể hiển thị thông báo hoặc chuyển hướng nếu userId không có
            Toast.makeText(getContext(), "Không tìm thấy ID người dùng. Trận đấu cá nhân có thể không khả dụng.", Toast.LENGTH_LONG).show();
        }


        allMatchesData = new HashMap<>(); // Khởi tạo cache
        displayedMatchesList = new ArrayList<>(); // Danh sách hiển thị

        // Thiết lập RecyclerView cho ngày
        datesList = generateDates();
        recyclerViewDates.post(() -> {
            int recyclerViewWidth = recyclerViewDates.getWidth();
            dateAdapter = new DateAdapter(datesList, this, recyclerViewWidth);
            recyclerViewDates.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            recyclerViewDates.setAdapter(dateAdapter);
            selectToday(); // Chọn ngày hôm nay khi khởi tạo
        });

        // Thiết lập RecyclerView cho các trận đấu
        matchAdapter = new MatchAdapter(displayedMatchesList);
        recyclerViewMatches.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewMatches.setAdapter(matchAdapter);

        // Xử lý sự kiện click cho bộ lọc trạng thái
        filterStatusContainer.setOnClickListener(v -> showStatusFilterDialog());

        // Xử lý sự kiện click cho nút thêm trận đấu
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

        // Thiết lập các tab "Tổng quát cả clb" và "Cá nhân"
        setupTabLayout();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (btnAddMatch != null) {
            btnAddMatch.setVisibility(View.VISIBLE);
        }
        // Đảm bảo tab được chọn đúng khi fragment được resume
        updateTabSelection(currentMatchType);
        // Tải lại trận đấu cho ngày và loại trận đấu hiện tại
        if (currentSelectedDateKey != null) {
            loadMatchesForSelectedDate(currentSelectedDateKey, currentMatchType);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (btnAddMatch != null) {
            btnAddMatch.setVisibility(View.GONE);
        }
    }

    /**
     * Thiết lập chức năng chuyển đổi giữa các tab "Tổng quát cả clb" và "Cá nhân".
     */
    private void setupTabLayout() {
        // Kích hoạt animation chuyển đổi cho LayoutTransition của LinearLayout cha
        if (item1 != null && item1.getParent() instanceof LinearLayout) {
            ((LinearLayout) item1.getParent()).getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        }

        // Xử lý click cho tab "Tổng quát cả clb"
        if (item1 != null) {
            item1.setOnClickListener(v -> {
                if (!currentMatchType.equals("club_all")) {
                    currentMatchType = "club_all"; // Cập nhật loại trận đấu
                    updateTabSelection(currentMatchType); // Cập nhật giao diện tab
                    if (currentSelectedDateKey != null) {
                        // Tải lại trận đấu cho ngày hiện tại với loại "Tổng quát cả clb"
                        loadMatchesForSelectedDate(currentSelectedDateKey, currentMatchType);
                    }
                }
            });
        }

        // Xử lý click cho tab "Cá nhân"
        if (item2 != null) {
            item2.setOnClickListener(v -> {
                if (!currentMatchType.equals("user_individual")) {
                    currentMatchType = "user_individual"; // Cập nhật loại trận đấu
                    updateTabSelection(currentMatchType); // Cập nhật giao diện tab
                    if (currentSelectedDateKey != null) {
                        // Tải lại trận đấu cho ngày hiện tại với loại "Cá nhân"
                        loadMatchesForSelectedDate(currentSelectedDateKey, currentMatchType);
                    }
                }
            });
        }

        // Cập nhật giao diện tab lần đầu khi khởi tạo
        updateTabSelection(currentMatchType);
    }

    /**
     * Cập nhật giao diện của thanh chọn tab (màu chữ và vị trí thanh `select`).
     * @param selectedType Loại trận đấu được chọn ("club_all" hoặc "user_individual").
     */
    private void updateTabSelection(String selectedType) {
        if (tabContentFrame == null || select == null || item1 == null || item2 == null) {
            Log.e("MatchesFragment", "Tab layout views not initialized.");
            return;
        }

        // Đảm bảo tabContentFrame đã có chiều rộng để tính toán vị trí thanh select
        if (tabContentFrame.getWidth() == 0) {
            tabContentFrame.post(() -> updateTabSelection(selectedType));
            return;
        }

        int tabWidth = tabContentFrame.getWidth() / 2; // Chiều rộng của mỗi tab
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) select.getLayoutParams();
        params.width = tabWidth; // Đặt chiều rộng cho thanh select

        if (selectedType.equals("club_all")) {
            params.setMarginStart(0); // Đặt vị trí cho tab "Tổng quát cả clb"
            select.setLayoutParams(params);
            item1.setTextColor(Color.WHITE); // Đổi màu chữ tab được chọn
            item2.setTextColor(Color.BLACK); // Đổi màu chữ tab không được chọn
        } else if (selectedType.equals("user_individual")) {
            params.setMarginStart(tabWidth); // Đặt vị trí cho tab "Cá nhân"
            select.setLayoutParams(params);
            item1.setTextColor(Color.BLACK);
            item2.setTextColor(Color.WHITE);
        }
    }

    // Phương thức tạo danh sách ngày (không thay đổi)
    private List<DateItem> generateDates() {
        List<DateItem> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -10);

        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", new Locale("vi", "VN"));

        for (int i = 0; i < 30; i++) {
            String dayOfMonth = dayFormat.format(calendar.getTime());
            String month = monthFormat.format(calendar.getTime());
            DateItem dateItem = new DateItem(dayOfMonth, month);
            dates.add(dateItem);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return dates;
    }

    // Phương thức chọn ngày hôm nay (không thay đổi)
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
            selectedCal.set(Calendar.YEAR, now.get(Calendar.YEAR)); // Đặt năm hiện tại
        } catch (java.text.ParseException e) {
            Log.e("MatchesFragment", "Lỗi phân tích cú pháp tháng khi chọn ngày: " + e.getMessage());
            selectedCal = Calendar.getInstance(); // Fallback về ngày hôm nay
        }

        SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        currentSelectedDateKey = fullDateFormat.format(selectedCal.getTime());

        // Tải trận đấu dựa trên ngày và loại trận đấu hiện tại
        loadMatchesForSelectedDate(currentSelectedDateKey, currentMatchType);
    }

    // Phương thức hiển thị dialog lọc trạng thái (không thay đổi)
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

    /**
     * Tải danh sách trận đấu cho ngày đã chọn và loại trận đấu đã chọn (tổng quát hay cá nhân).
     * Dữ liệu được cache để tránh gọi API lặp lại.
     * @param dateKey Chuỗi ngày ở định dạng "yyyy-MM-dd".
     * @param matchType Loại trận đấu ("club_all" cho tổng quát, "user_individual" cho cá nhân).
     */
    private void loadMatchesForSelectedDate(String dateKey, String matchType) {
        // Tạo khóa tổng hợp cho cache (ví dụ: "2025-06-22_club_all" hoặc "2025-06-22_user_individual")
        String compositeKey = dateKey + "_" + matchType;

        // Kiểm tra cache trước
        if (allMatchesData.containsKey(compositeKey)) {
            // Dữ liệu có trong cache, chuyển đổi và hiển thị
            List<Match> cachedMatches = allMatchesData.get(compositeKey);
            Log.d("MatchesFragment", "Tìm thấy " + cachedMatches.size() + " trận đấu cho " + compositeKey + " từ cache.");
            convertAndDisplayMatches(cachedMatches); // Chuyển đổi và hiển thị các trận đấu
        } else {
            // Dữ liệu không có trong cache, lấy từ API
            Log.d("MatchesFragment", "Đang lấy trận đấu cho " + compositeKey + " từ API.");
            int page = 1; // Mặc định trang 1
            int pageSize = 50; // Mặc định kích thước trang 50

            Call<MatchResponse> call;
            if (matchType.equals("club_all")) {
                // Gọi API để lấy tất cả trận đấu trong ngày
                call = matchService.getMatchesByDay(dateKey, page, pageSize);
            } else if (matchType.equals("user_individual")) {
                // Kiểm tra userId trước khi gọi API cá nhân
                if (currentUserId == null || currentUserId.isEmpty()) {
                    Toast.makeText(getContext(), "ID người dùng không khả dụng để tải trận đấu cá nhân.", Toast.LENGTH_SHORT).show();
                    displayedMatchesList.clear(); // Xóa danh sách hiển thị
                    matchAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView
                    return;
                }
                // Gọi API để lấy trận đấu của người dùng trong ngày
                call = matchService.getMatchesByDayAndUser(dateKey, currentUserId, page, pageSize);
            } else {
                Log.e("MatchesFragment", "Loại trận đấu không hợp lệ: " + matchType);
                Toast.makeText(getContext(), "Loại trận đấu được chọn không hợp lệ.", Toast.LENGTH_SHORT).show();
                displayedMatchesList.clear();
                matchAdapter.notifyDataSetChanged();
                return;
            }

            // Thực hiện cuộc gọi API
            call.enqueue(new Callback<MatchResponse>() {
                @Override
                public void onResponse(Call<MatchResponse> call, Response<MatchResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Match> fetchedMatches = response.body().getMatches();
                        allMatchesData.put(compositeKey, fetchedMatches); // Lưu dữ liệu API thô vào cache
                        convertAndDisplayMatches(fetchedMatches); // Chuyển đổi và hiển thị
                        Log.d("MatchesFragment", "Đã lấy " + fetchedMatches.size() + " trận đấu cho " + compositeKey + " từ API.");
                    } else {
                        Log.e("MatchesFragment", "Cuộc gọi API thất bại cho " + matchType + ": " + response.message());
                        Toast.makeText(getContext(), "Tải trận đấu thất bại: " + response.message(), Toast.LENGTH_SHORT).show();
                        displayedMatchesList.clear();
                        matchAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<MatchResponse> call, Throwable t) {
                    Log.e("MatchesFragment", "Lỗi cuộc gọi API cho " + matchType + ": " + t.getMessage(), t);
                    Toast.makeText(getContext(), "Lỗi khi tải trận đấu: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    displayedMatchesList.clear();
                    matchAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    /**
     * Chuyển đổi các đối tượng Match thô từ API thành đối tượng Matches hiển thị
     * và cập nhật giao diện người dùng.
     * @param rawMatches Danh sách các đối tượng Match thô từ API.
     */
    private void convertAndDisplayMatches(List<Match> rawMatches) {
        List<Matches> convertedMatches = new ArrayList<>();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        // Định dạng cho dấu thời gian ISO 8601 của API
        SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        apiDateFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC")); // Thời gian API là UTC

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

            // Sắp xếp người tham gia theo tên đầy đủ để đảm bảo thứ tự nhất quán cho tên đội
            Collections.sort(team1Participants, Comparator.comparing(Participant::getFullName));
            Collections.sort(team2Participants, Comparator.comparing(Participant::getFullName));

            // Xác định xem đó là trận đấu đơn hay đôi và hiển thị tên phù hợp
            // Dựa vào số lượng người tham gia trong mỗi đội để quyết định loại trận đấu
            if (team1Participants.size() == 1 && team2Participants.size() == 1) {
                player1DisplayName = team1Participants.get(0).getFullName();
                player2DisplayName = team2Participants.get(0).getFullName();
            } else { // Trận đấu đôi hoặc phức tạp hơn
                List<String> team1Names = new ArrayList<>();
                for (Participant p : team1Participants) {
                    team1Names.add(p.getFullName());
                }
                player1DisplayName = String.join(" & ", team1Names); // Nối tên cho trận đấu đôi

                List<String> team2Names = new ArrayList<>();
                for (Participant p : team2Participants) {
                    team2Names.add(p.getFullName());
                }
                player2DisplayName = String.join(" & ", team2Names); // Nối tên cho trận đấu đôi
            }

            // Phân tích và định dạng thời gian trận đấu
            if (rawMatch.getStartTime() != null && !rawMatch.getStartTime().isEmpty()) {
                try {
                    Date date = apiDateFormat.parse(rawMatch.getStartTime());
                    matchTimeDisplay = timeFormat.format(date); // Định dạng sang giờ địa phương
                } catch (java.text.ParseException e) {
                    Log.e("MatchesFragment", "Lỗi phân tích cú pháp thời gian bắt đầu trận đấu: " + rawMatch.getStartTime(), e);
                }
            }

            // Sử dụng avatar_1 làm placeholder vì URL avatar thực tế không có trong phản hồi API
            convertedMatches.add(new Matches(player1DisplayName, player2DisplayName,
                    R.drawable.avatar_1, R.drawable.avatar_1, // Thay bằng avatar thực tế nếu có
                    scoreDisplay, matchTimeDisplay, matchStatusDisplay));
        }

        displayedMatchesList.clear();
        displayedMatchesList.addAll(convertedMatches);
        filterMatchesByStatus(currentSelectedFilter); // Áp dụng lại bộ lọc sau khi chuyển đổi dữ liệu
    }

    /**
     * Ánh xạ các chuỗi trạng thái API sang các chuỗi trạng thái tiếng Việt hiển thị.
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
                return apiStatus; // Trả về nguyên bản nếu không tìm thấy ánh xạ
        }
    }

    /**
     * Lọc danh sách trận đấu đang hiển thị theo trạng thái.
     * @param statusFilter Trạng thái để lọc (ví dụ: "Tất cả", "Sắp diễn ra", v.v.).
     */
    private void filterMatchesByStatus(String statusFilter) {
        List<Matches> filteredList = new ArrayList<>();
        String compositeKey = currentSelectedDateKey + "_" + currentMatchType;
        List<Match> rawMatchesForCurrentDateAndType = allMatchesData.get(compositeKey);

        if (rawMatchesForCurrentDateAndType != null) {
            // Luôn chuyển đổi từ dữ liệu thô đã cache để đảm bảo không bị mất dữ liệu
            List<Matches> tempConvertedList = new ArrayList<>();
            SimpleDateFormat tempTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            SimpleDateFormat tempApiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            tempApiDateFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));

            for (Match rawMatch : rawMatchesForCurrentDateAndType) {
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

                Collections.sort(team1Participants, Comparator.comparing(Participant::getFullName));
                Collections.sort(team2Participants, Comparator.comparing(Participant::getFullName));

                if (team1Participants.size() == 1 && team2Participants.size() == 1) {
                    player1DisplayName = team1Participants.get(0).getFullName();
                    player2DisplayName = team2Participants.get(0).getFullName();
                } else {
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

                if (rawMatch.getStartTime() != null && !rawMatch.getStartTime().isEmpty()) {
                    try {
                        Date date = tempApiDateFormat.parse(rawMatch.getStartTime());
                        matchTimeDisplay = tempTimeFormat.format(date);
                    } catch (java.text.ParseException e) {
                        Log.e("MatchesFragment", "Lỗi phân tích cú pháp thời gian bắt đầu trận đấu (lọc): " + rawMatch.getStartTime(), e);
                    }
                }
                Matches convertedMatch = new Matches(player1DisplayName, player2DisplayName,
                        R.drawable.avatar_1, R.drawable.avatar_1,
                        scoreDisplay, matchTimeDisplay, matchStatusDisplay);

                // Áp dụng bộ lọc
                if (statusFilter.equals("Tất cả") || convertedMatch.getMatchStatus().equals(statusFilter)) {
                    filteredList.add(convertedMatch);
                }
            }
        }
        displayedMatchesList.clear();
        displayedMatchesList.addAll(filteredList);
        matchAdapter.notifyDataSetChanged();
    }
}