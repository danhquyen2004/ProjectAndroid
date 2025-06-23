package com.example.tlupickleball.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView; // Import ImageView

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide; // Import Glide
import com.example.tlupickleball.R;
import com.example.tlupickleball.fragments.PlayerSelectionDialog;
import com.example.tlupickleball.model.User;
import com.example.tlupickleball.network.api_model.match.CreateMatchRequest;
import com.example.tlupickleball.network.core.ApiClient;
import com.example.tlupickleball.network.service.MatchService;
import com.example.tlupickleball.network.api_model.match.CreateMatchResponse;


import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Đảm bảo AddMatch_Fragment triển khai PlayerSelectionDialog.OnPlayerSelectedInDialogListener
public class AddMatch_Fragment extends Fragment implements PlayerSelectionDialog.OnPlayerSelectedInDialogListener {

    private ImageButton backButton;
    private Button inputTimeButton, selectDateButton, confirmButton;
    private TextView item1, item2;
    private View tabIndicator;
    private FrameLayout frameLayoutTabContainer;
    private Spinner setCountSpinner;
    private LinearLayout layoutMatchSetScoresContainer;

    // Khai báo các TextView và ImageButton cho người chơi trên sân
    private TextView player1Team1Name, player2Team2Name, player2Team1Name, player1Team2Name;
    private ImageButton player1Team1AddButton, player2Team2AddButton, player2Team1AddButton, player1Team2AddButton;

    // Khai báo ImageView cho avatar người chơi
    private ImageView player1Team1Avatar, player2Team2Avatar, player2Team1Avatar, player1Team2Avatar;

    // Lưu trữ thông tin người chơi đã chọn (có thể là User object hoặc chỉ Uid)
    private User selectedPlayer1Team1;
    private User selectedPlayer2Team2;
    private User selectedPlayer2Team1; // Cho đấu đôi
    private User selectedPlayer1Team2; // Cho đấu đôi

    private Calendar selectedDateTime;
    private int selectedSetCount;
    private boolean isSinglesMatch = true; // Mặc định là đấu đơn

    // Biến để theo dõi slot người chơi đang được chọn
    private int currentSelectedPlayerSlot = -1; // 0: Player1 Team1, 1: Player2 Team2, 2: Player2 Team1, 3: Player1 Team2

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_match, container, false);
        initViews(view);
        setupListeners();
        updateDateButton(selectedDateTime.getTime());
        updateTimeButton(selectedDateTime.getTime()); // Cập nhật thời gian ban đầu
        updateSetScoreViews();
        // Cần đảm bảo tab ban đầu được chọn đúng màu và vị trí
        frameLayoutTabContainer.post(() -> selectTab(isSinglesMatch));
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedDateTime = Calendar.getInstance();
        selectedSetCount = 1;
    }

    private void initViews(View view) {
        backButton = view.findViewById(R.id.backButton);
        inputTimeButton = view.findViewById(R.id.inputTimeButton);
        selectDateButton = view.findViewById(R.id.selectDateButton);
        confirmButton = view.findViewById(R.id.confirmButton);

        item1 = view.findViewById(R.id.item1);
        item2 = view.findViewById(R.id.item2);
        tabIndicator = view.findViewById(R.id.tab_indicator);
        frameLayoutTabContainer = view.findViewById(R.id.frame_layout_tab_container);

        setCountSpinner = view.findViewById(R.id.setCountSpinner);
        layoutMatchSetScoresContainer = view.findViewById(R.id.layoutMatchSetScoresContainer);

        // Ánh xạ các TextView, ImageButton và ImageView cho người chơi từ view_pickleball_court.xml
        player1Team1Name = view.findViewById(R.id.player1_team1_name);
        player2Team2Name = view.findViewById(R.id.player2_team2_name);
        player2Team1Name = view.findViewById(R.id.player2_team1_name);
        player1Team2Name = view.findViewById(R.id.player1_team2_name);

        player1Team1AddButton = view.findViewById(R.id.player1_team1_add_button);
        player2Team2AddButton = view.findViewById(R.id.player2_team2_add_button);
        player2Team1AddButton = view.findViewById(R.id.player2_team1_add_button);
        player1Team2AddButton = view.findViewById(R.id.player1_team2_add_button);

        // Ánh xạ ImageView cho avatar
        player1Team1Avatar = view.findViewById(R.id.player1_team1_avatar);
        player2Team2Avatar = view.findViewById(R.id.player2_team2_avatar);
        player2Team1Avatar = view.findViewById(R.id.player2_team1_avatar);
        player1Team2Avatar = view.findViewById(R.id.player1_team2_avatar);
    }

    private void setupListeners() {
        backButton.setOnClickListener(v -> requireActivity().onBackPressed());
        inputTimeButton.setOnClickListener(v -> showTimePicker());
        selectDateButton.setOnClickListener(v -> showDatePicker());
        confirmButton.setOnClickListener(v -> recordMatch());

        item1.setOnClickListener(v -> selectTab(true));
        item2.setOnClickListener(v -> selectTab(false));

        setCountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSetCount = Integer.parseInt(parent.getItemAtPosition(position).toString());
                updateSetScoreViews();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Đặt listener cho các nút thêm người chơi và truyền slot tương ứng
        player1Team1AddButton.setOnClickListener(v -> {
            currentSelectedPlayerSlot = 0; // Slot 0: Player 1 Team 1
            showPlayerSelectionDialog();
        });
        player2Team2AddButton.setOnClickListener(v -> {
            currentSelectedPlayerSlot = 1; // Slot 1: Player 2 Team 2
            showPlayerSelectionDialog();
        });
        player2Team1AddButton.setOnClickListener(v -> {
            currentSelectedPlayerSlot = 2; // Slot 2: Player 2 Team 1 (cho đấu đôi)
            showPlayerSelectionDialog();
        });
        player1Team2AddButton.setOnClickListener(v -> {
            currentSelectedPlayerSlot = 3; // Slot 3: Player 1 Team 2 (cho đấu đôi)
            showPlayerSelectionDialog();
        });
    }

    private void showPlayerSelectionDialog() {
        PlayerSelectionDialog dialog = new PlayerSelectionDialog();
        Bundle args = new Bundle();
        args.putInt("playerSlot", currentSelectedPlayerSlot); // Truyền slot hiện tại vào dialog
        dialog.setArguments(args);
        // RẤT QUAN TRỌNG: Đặt listener là chính AddMatch_Fragment này
        dialog.setOnPlayerSelectedInDialogListener(this);
        dialog.show(getParentFragmentManager(), "PlayerSelectionDialog");
    }

    // Ghi đè phương thức từ PlayerSelectionDialog.OnPlayerSelectedInDialogListener
    @Override
    public void onPlayerSelected(User player, int playerSlot) {
        Log.d("AddMatch_Fragment", "onPlayerSelected called. Player: " + (player != null ? player.getFullName() : "null") + ", Slot: " + playerSlot);
        String abbreviatedName = "";
        if (player != null) {
            abbreviatedName = abbreviateName(player.getFullName());
        }
        Log.d("AddMatch_Fragment", "Abbreviated Name: " + abbreviatedName);

        // Cập nhật TextView và lưu trữ User object dựa trên playerSlot
        // Đồng thời tải ảnh đại diện vào ImageView
        switch (playerSlot) {
            case 0:
                player1Team1Name.setText(abbreviatedName);
                selectedPlayer1Team1 = player;
                loadPlayerAvatar(player, player1Team1Avatar);
                break;
            case 1:
                player2Team2Name.setText(abbreviatedName);
                selectedPlayer2Team2 = player;
                loadPlayerAvatar(player, player2Team2Avatar);
                break;
            case 2:
                player2Team1Name.setText(abbreviatedName);
                selectedPlayer2Team1 = player;
                loadPlayerAvatar(player, player2Team1Avatar);
                break;
            case 3:
                player1Team2Name.setText(abbreviatedName);
                selectedPlayer1Team2 = player;
                loadPlayerAvatar(player, player1Team2Avatar);
                break;
        }
        currentSelectedPlayerSlot = -1; // Reset slot sau khi chọn
    }

    private void loadPlayerAvatar(User player, ImageView imageView) {
        if (player != null && player.getAvatarUrl() != null && !player.getAvatarUrl().isEmpty()) {
            Glide.with(this)
                    .load(player.getAvatarUrl())
                    .into(imageView);
        } else {
        }
    }


    private String abbreviateName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "";
        }
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length > 0) {
            StringBuilder abbreviated = new StringBuilder();
            // Lặp qua tất cả các từ trừ từ cuối cùng, lấy chữ cái đầu và thêm dấu chấm
            for (int i = 0; i < parts.length - 1; i++) {
                if (!parts[i].isEmpty()) {
                    abbreviated.append(parts[i].charAt(0)).append(".");
                }
            }
            // Thêm từ cuối cùng
            abbreviated.append(parts[parts.length - 1]);
            return abbreviated.toString();
        }
        return fullName; // Trường hợp không có khoảng trắng hoặc chỉ có một từ
    }


    private void showTimePicker() {
        int hour = selectedDateTime.get(Calendar.HOUR_OF_DAY);
        int minute = selectedDateTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                (view, hourOfDay, minuteOfHour) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDateTime.set(Calendar.MINUTE, minuteOfHour);
                    updateTimeButton(selectedDateTime.getTime());
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void updateTimeButton(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        inputTimeButton.setText(sdf.format(date));
    }

    private void showDatePicker() {
        int year = selectedDateTime.get(Calendar.YEAR);
        int month = selectedDateTime.get(Calendar.MONTH);
        int day = selectedDateTime.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year1, monthOfYear, dayOfMonth) -> {
                    selectedDateTime.set(year1, monthOfYear, dayOfMonth);
                    updateDateButton(selectedDateTime.getTime());
                }, year, month, day);
        datePickerDialog.show();
    }

    private void updateDateButton(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        selectDateButton.setText(sdf.format(date));
    }

    private void selectTab(boolean isSingles) {
        this.isSinglesMatch = isSingles;

        if (frameLayoutTabContainer.getWidth() == 0) {
            frameLayoutTabContainer.post(() -> animateTabIndicator(isSingles));
        } else {
            animateTabIndicator(isSingles);
        }

        // Cập nhật màu chữ
        if (isSingles) {
            item1.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            item2.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        } else {
            item1.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
            item2.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        }

        updateCourtView();
    }

    private void animateTabIndicator(boolean isSingles) {
        int totalWidth = frameLayoutTabContainer.getWidth();
        int tabWidth = totalWidth / 2;

        tabIndicator.setPivotX(0f);
        tabIndicator.setScaleX(0.5f);

        float targetX = isSingles ? 0f : tabWidth;

        Log.d("TabLayoutDebug", "frameLayoutTabContainer Width: " + totalWidth);
        Log.d("TabLayoutDebug", "Tab Width: " + tabWidth);
        Log.d("TabLayoutDebug", "Target X: " + targetX);
        Log.d("TabLayoutDebug", "Is Singles Match: " + isSingles);

        tabIndicator.animate()
                .translationX(targetX)
                .setDuration(200)
                .start();
    }

    private void updateCourtView() {
        if (isSinglesMatch) {
            player2Team1Name.setVisibility(View.GONE);
            player1Team2Name.setVisibility(View.GONE);
            player2Team1AddButton.setVisibility(View.GONE);
            player1Team2AddButton.setVisibility(View.GONE);
            player2Team1Avatar.setVisibility(View.GONE); // Ẩn avatar
            player1Team2Avatar.setVisibility(View.GONE); // Ẩn avatar

            // Xóa tên và đối tượng User của người chơi team đôi khi chuyển sang đấu đơn
            player2Team1Name.setText("");
            player1Team2Name.setText("");
            selectedPlayer2Team1 = null;
            selectedPlayer1Team2 = null;

        } else {
            player2Team1Name.setVisibility(View.VISIBLE);
            player1Team2Name.setVisibility(View.VISIBLE);
            player2Team1AddButton.setVisibility(View.VISIBLE);
            player1Team2AddButton.setVisibility(View.VISIBLE);
            player2Team1Avatar.setVisibility(View.VISIBLE); // Hiện avatar
            player1Team2Avatar.setVisibility(View.VISIBLE); // Hiện avatar
        }
    }

    private void updateSetScoreViews() {
        layoutMatchSetScoresContainer.removeAllViews();
        for (int i = 0; i < selectedSetCount; i++) {
            View setScoreView = LayoutInflater.from(requireContext()).inflate(R.layout.item_match_set_score, layoutMatchSetScoresContainer, false);

            TextView setNumber = setScoreView.findViewById(R.id.textViewSetLabel);
            EditText team1Score = setScoreView.findViewById(R.id.editTextScoreTeam1);
            EditText team2Score = setScoreView.findViewById(R.id.editTextScoreTeam2);

            setNumber.setText(String.format(Locale.getDefault(), "Set %d", i + 1));
            layoutMatchSetScoresContainer.addView(setScoreView);
        }
    }

    private void recordMatch() {
        Date matchDate = selectedDateTime.getTime();
        String matchType = isSinglesMatch ? "SINGLES" : "DOUBLES";
        int numOfSets = selectedSetCount;

        List<String> team1PlayerIds = new ArrayList<>();
        List<String> team2PlayerIds = new ArrayList<>();

        // Thêm người chơi vào danh sách UIDs dựa trên loại trận đấu và các đối tượng User đã chọn
        if (selectedPlayer1Team1 != null) {
            team1PlayerIds.add(selectedPlayer1Team1.getUid());
        }
        if (!isSinglesMatch && selectedPlayer2Team1 != null) {
            team1PlayerIds.add(selectedPlayer2Team1.getUid());
        }

        if (selectedPlayer2Team2 != null) {
            team2PlayerIds.add(selectedPlayer2Team2.getUid());
        }
        if (!isSinglesMatch && selectedPlayer1Team2 != null) {
            team2PlayerIds.add(selectedPlayer1Team2.getUid());
        }

        // Kiểm tra số lượng người chơi đã chọn
        if (isSinglesMatch) {
            if (team1PlayerIds.size() != 1 || team2PlayerIds.size() != 1) {
                Toast.makeText(requireContext(), "Vui lòng chọn đủ 2 người chơi cho trận đấu đơn.", Toast.LENGTH_SHORT).show();
                return;
            }
        } else { // Đấu đôi
            if (team1PlayerIds.size() != 2 || team2PlayerIds.size() != 2) {
                Toast.makeText(requireContext(), "Vui lòng chọn đủ 4 người chơi cho trận đấu đôi.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        CreateMatchRequest.Teams teams = new CreateMatchRequest.Teams(team1PlayerIds, team2PlayerIds);

        List<CreateMatchRequest.SetResult> setResults = new ArrayList<>();
        for (int i = 0; i < layoutMatchSetScoresContainer.getChildCount(); i++) {
            View setView = layoutMatchSetScoresContainer.getChildAt(i);
            EditText team1ScoreEt = setView.findViewById(R.id.editTextScoreTeam1);
            EditText team2ScoreEt = setView.findViewById(R.id.editTextScoreTeam2);

            try {
                int team1Score = Integer.parseInt(team1ScoreEt.getText().toString());
                int team2Score = Integer.parseInt(team2ScoreEt.getText().toString());
                setResults.add(new CreateMatchRequest.SetResult(i + 1, team1Score, team2Score));
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Vui lòng nhập điểm số hợp lệ cho tất cả các set.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (setResults.isEmpty() && numOfSets > 0) {
            Toast.makeText(requireContext(), "Vui lòng nhập điểm số cho tất cả các set.", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String formattedDate = dateFormat.format(matchDate);
        String formattedTime = timeFormat.format(matchDate);

        CreateMatchRequest request = new CreateMatchRequest(formattedDate, formattedTime, matchType, numOfSets, teams, setResults);

        MatchService matchService = ApiClient.getClient(requireContext()).create(MatchService.class);
        matchService.createMatch(request).enqueue(new Callback<CreateMatchResponse>() {
            @Override
            public void onResponse(@NonNull Call<CreateMatchResponse> call, @NonNull Response<CreateMatchResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Trận đấu đã được tạo thành công!", Toast.LENGTH_SHORT).show();
                    if (response.body() != null) {
                        Log.d("AddMatch_Fragment", "API Response: " + new Gson().toJson(response.body()));
                    }
                    FragmentManager fragmentManager = getParentFragmentManager();
                    if (fragmentManager.getBackStackEntryCount() > 0) {
                        fragmentManager.popBackStack();
                    }
                } else {
                    String errorBody = "Không có lỗi phản hồi.";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e("AddMatch_Fragment", "Lỗi đọc errorBody: " + e.getMessage());
                    }
                    Toast.makeText(requireContext(), "Tạo trận đấu thất bại: " + response.code() + " - " + errorBody, Toast.LENGTH_LONG).show();
                    Log.e("AddMatch_Fragment", "Lỗi tạo trận đấu: " + response.code() + " - " + errorBody);
                }
            }

            @Override
            public void onFailure(Call<CreateMatchResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("AddMatch_Fragment", "Lỗi API network: " + t.getMessage(), t);
            }
        });
    }
}