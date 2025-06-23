package com.example.tlupickleball.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.tlupickleball.R;
import com.example.tlupickleball.model.User;
import com.example.tlupickleball.network.api_model.match.CreateMatchRequest;
import com.example.tlupickleball.network.api_model.match.CreateMatchResponse;
import com.example.tlupickleball.network.core.ApiClient;
import com.example.tlupickleball.network.service.MatchService;
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

public class AddMatch_Fragment extends Fragment implements PlayerSelectionDialog.OnPlayerSelectedInDialogListener {

    private ImageButton backButton;
    private Button inputTimeButton, selectDateButton, confirmButton;
    private TextView item1, item2;
    private View tabIndicator;
    private FrameLayout frameLayoutTabContainer;
    private Spinner setCountSpinner;
    private LinearLayout layoutMatchSetScoresContainer;
    private TextView player1Team1Name, player2Team2Name, player2Team1Name, player1Team2Name;
    private ImageButton player1Team1AddButton, player2Team2AddButton, player2Team1AddButton, player1Team2AddButton;
    private ImageView player1Team1Avatar, player2Team2Avatar, player2Team1Avatar, player1Team2Avatar;
    private User selectedPlayer1Team1;
    private User selectedPlayer2Team2;
    private User selectedPlayer2Team1;
    private User selectedPlayer1Team2;
    private Calendar selectedDateTime;
    private int selectedSetCount;
    private boolean isSinglesMatch = true;
    private int currentSelectedPlayerSlot = -1;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_match, container, false);
        initViews(view);
        setupListeners();
        updateDateButton(selectedDateTime.getTime());
        updateTimeButton(selectedDateTime.getTime());
        updateSetScoreViews();
        frameLayoutTabContainer.post(() -> selectTab(isSinglesMatch));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Xử lý nút back của hệ thống
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBackPress();
            }
        });
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
        player1Team1Name = view.findViewById(R.id.player1_team1_name);
        player2Team2Name = view.findViewById(R.id.player2_team2_name);
        player2Team1Name = view.findViewById(R.id.player2_team1_name);
        player1Team2Name = view.findViewById(R.id.player1_team2_name);
        player1Team1AddButton = view.findViewById(R.id.player1_team1_add_button);
        player2Team2AddButton = view.findViewById(R.id.player2_team2_add_button);
        player2Team1AddButton = view.findViewById(R.id.player2_team1_add_button);
        player1Team2AddButton = view.findViewById(R.id.player1_team2_add_button);
        player1Team1Avatar = view.findViewById(R.id.player1_team1_avatar);
        player2Team2Avatar = view.findViewById(R.id.player2_team2_avatar);
        player2Team1Avatar = view.findViewById(R.id.player2_team1_avatar);
        player1Team2Avatar = view.findViewById(R.id.player1_team2_avatar);
    }

    private void setupListeners() {
        // Cập nhật listener của nút back trên toolbar
        backButton.setOnClickListener(v -> handleBackPress());

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
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        player1Team1AddButton.setOnClickListener(v -> { currentSelectedPlayerSlot = 0; showPlayerSelectionDialog(); });
        player2Team2AddButton.setOnClickListener(v -> { currentSelectedPlayerSlot = 1; showPlayerSelectionDialog(); });
        player2Team1AddButton.setOnClickListener(v -> { currentSelectedPlayerSlot = 2; showPlayerSelectionDialog(); });
        player1Team2AddButton.setOnClickListener(v -> { currentSelectedPlayerSlot = 3; showPlayerSelectionDialog(); });
    }

    /**
     * Phương thức xử lý chung cho mọi hành động back.
     */
    private void handleBackPress() {
        if (hasUnsavedData()) {
            showExitConfirmationDialog();
        } else {
            getParentFragmentManager().popBackStack();
        }
    }

    /**
     * Kiểm tra xem người dùng đã nhập dữ liệu hay chưa.
     */
    private boolean hasUnsavedData() {
        if (selectedPlayer1Team1 != null || selectedPlayer2Team1 != null ||
                selectedPlayer1Team2 != null || selectedPlayer2Team2 != null) {
            return true;
        }

        if (layoutMatchSetScoresContainer != null) {
            for (int i = 0; i < layoutMatchSetScoresContainer.getChildCount(); i++) {
                View setView = layoutMatchSetScoresContainer.getChildAt(i);
                EditText team1ScoreEt = setView.findViewById(R.id.editTextScoreTeam1);
                EditText team2ScoreEt = setView.findViewById(R.id.editTextScoreTeam2);
                if (!team1ScoreEt.getText().toString().isEmpty() || !team2ScoreEt.getText().toString().isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Hiển thị hộp thoại xác nhận thoát.
     */
    private void showExitConfirmationDialog() {
        // Bắt đầu xây dựng Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // Inflate (biến đổi) file XML thành một đối tượng View trong Java
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_custom_warning, null);

        // Gắn view tùy chỉnh này vào Dialog
        builder.setView(dialogView);

        // Tạo đối tượng Dialog
        final AlertDialog dialog = builder.create();

        // Ánh xạ các nút bấm từ view tùy chỉnh
        Button btnConfirm = dialogView.findViewById(R.id.btn_confirm_exit);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel_exit);

        // Gán sự kiện cho nút "Xác nhận"
        btnConfirm.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
            dialog.dismiss(); // Đóng dialog sau khi thoát
        });

        // Gán sự kiện cho nút "Quay lại"
        btnCancel.setOnClickListener(v -> {
            dialog.dismiss(); // Chỉ đóng dialog
        });

        // Đảm bảo dialog có nền trong suốt để thấy được bo góc của layout
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Hiển thị dialog
        dialog.show();
    }

    private void showPlayerSelectionDialog() {
        PlayerSelectionDialog dialog = new PlayerSelectionDialog();
        Bundle args = new Bundle();
        args.putInt("playerSlot", currentSelectedPlayerSlot);
        dialog.setArguments(args);
        dialog.setOnPlayerSelectedInDialogListener(this);
        dialog.show(getParentFragmentManager(), "PlayerSelectionDialog");
    }

    @Override
    public void onPlayerSelected(User player, int playerSlot) {
        String abbreviatedName = "";
        if (player != null) abbreviatedName = abbreviateName(player.getFullName());
        switch (playerSlot) {
            case 0: player1Team1Name.setText(abbreviatedName); selectedPlayer1Team1 = player; loadPlayerAvatar(player, player1Team1Avatar); break;
            case 1: player2Team2Name.setText(abbreviatedName); selectedPlayer2Team2 = player; loadPlayerAvatar(player, player2Team2Avatar); break;
            case 2: player2Team1Name.setText(abbreviatedName); selectedPlayer2Team1 = player; loadPlayerAvatar(player, player2Team1Avatar); break;
            case 3: player1Team2Name.setText(abbreviatedName); selectedPlayer1Team2 = player; loadPlayerAvatar(player, player1Team2Avatar); break;
        }
        currentSelectedPlayerSlot = -1;
    }

    private void loadPlayerAvatar(User player, ImageView imageView) {
        if (player != null && player.getAvatarUrl() != null && !player.getAvatarUrl().isEmpty()) {
            Glide.with(this).load(player.getAvatarUrl()).into(imageView);
        }
    }

    private String abbreviateName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) return "";
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length > 0) {
            StringBuilder abbreviated = new StringBuilder();
            for (int i = 0; i < parts.length - 1; i++) {
                if (!parts[i].isEmpty()) abbreviated.append(parts[i].charAt(0)).append(".");
            }
            abbreviated.append(parts[parts.length - 1]);
            return abbreviated.toString();
        }
        return fullName;
    }

    private void showTimePicker() { int hour = selectedDateTime.get(Calendar.HOUR_OF_DAY); int minute = selectedDateTime.get(Calendar.MINUTE); TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), (view, hourOfDay, minuteOfHour) -> { selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay); selectedDateTime.set(Calendar.MINUTE, minuteOfHour); updateTimeButton(selectedDateTime.getTime()); }, hour, minute, true); timePickerDialog.show(); }
    private void updateTimeButton(Date date) { SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault()); inputTimeButton.setText(sdf.format(date)); }
    private void showDatePicker() { int year = selectedDateTime.get(Calendar.YEAR); int month = selectedDateTime.get(Calendar.MONTH); int day = selectedDateTime.get(Calendar.DAY_OF_MONTH); DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year1, monthOfYear, dayOfMonth) -> { selectedDateTime.set(year1, monthOfYear, dayOfMonth); updateDateButton(selectedDateTime.getTime()); }, year, month, day); datePickerDialog.show(); }
    private void updateDateButton(Date date) { SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()); selectDateButton.setText(sdf.format(date)); }
    private void selectTab(boolean isSingles) { this.isSinglesMatch = isSingles; animateTabIndicator(isSingles); if (isSingles) { item1.setTextColor(ContextCompat.getColor(requireContext(), R.color.white)); item2.setTextColor(ContextCompat.getColor(requireContext(), R.color.black)); } else { item1.setTextColor(ContextCompat.getColor(requireContext(), R.color.black)); item2.setTextColor(ContextCompat.getColor(requireContext(), R.color.white)); } updateCourtView(); }
    private void animateTabIndicator(boolean isSingles) { int totalWidth = frameLayoutTabContainer.getWidth(); if (totalWidth == 0) { frameLayoutTabContainer.post(() -> animateTabIndicator(isSingles)); return; } int tabWidth = totalWidth / 2; ViewGroup.LayoutParams params = tabIndicator.getLayoutParams(); params.width = tabWidth; tabIndicator.setLayoutParams(params); float targetX = isSingles ? 0f : tabWidth; tabIndicator.animate().translationX(targetX).setDuration(300).start(); }
    private void updateCourtView() { if (isSinglesMatch) { player2Team1Name.setVisibility(View.GONE); player1Team2Name.setVisibility(View.GONE); player2Team1AddButton.setVisibility(View.GONE); player1Team2AddButton.setVisibility(View.GONE); player2Team1Avatar.setVisibility(View.GONE); player1Team2Avatar.setVisibility(View.GONE); player2Team1Name.setText(""); player1Team2Name.setText(""); selectedPlayer2Team1 = null; selectedPlayer1Team2 = null; } else { player2Team1Name.setVisibility(View.VISIBLE); player1Team2Name.setVisibility(View.VISIBLE); player2Team1AddButton.setVisibility(View.VISIBLE); player1Team2AddButton.setVisibility(View.VISIBLE); player2Team1Avatar.setVisibility(View.VISIBLE); player1Team2Avatar.setVisibility(View.VISIBLE); } }
    private void updateSetScoreViews() { layoutMatchSetScoresContainer.removeAllViews(); for (int i = 0; i < selectedSetCount; i++) { View setScoreView = LayoutInflater.from(requireContext()).inflate(R.layout.item_match_set_score, layoutMatchSetScoresContainer, false); TextView setNumber = setScoreView.findViewById(R.id.textViewSetLabel); setNumber.setText(String.format(Locale.getDefault(), "Set %d", i + 1)); layoutMatchSetScoresContainer.addView(setScoreView); } }

    private void recordMatch() {
        Date matchDateTime = selectedDateTime.getTime();
        String matchType = isSinglesMatch ? "SINGLES" : "DOUBLES";
        int numOfSets = selectedSetCount;
        List<String> team1PlayerIds = new ArrayList<>();
        List<String> team2PlayerIds = new ArrayList<>();
        if (selectedPlayer1Team1 != null) team1PlayerIds.add(selectedPlayer1Team1.getUid());
        if (!isSinglesMatch && selectedPlayer2Team1 != null) team1PlayerIds.add(selectedPlayer2Team1.getUid());
        if (selectedPlayer2Team2 != null) team2PlayerIds.add(selectedPlayer2Team2.getUid());
        if (!isSinglesMatch && selectedPlayer1Team2 != null) team2PlayerIds.add(selectedPlayer1Team2.getUid());
        if (isSinglesMatch) {
            if (team1PlayerIds.size() != 1 || team2PlayerIds.size() != 1) {
                Toast.makeText(requireContext(), "Vui lòng chọn đủ 2 người chơi cho trận đấu đơn.", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            if (team1PlayerIds.size() != 2 || team2PlayerIds.size() != 2) {
                Toast.makeText(requireContext(), "Vui lòng chọn đủ 4 người chơi cho trận đấu đôi.", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        CreateMatchRequest.Teams teams = new CreateMatchRequest.Teams(team1PlayerIds, team2PlayerIds);
        List<CreateMatchRequest.SetResult> setResults = new ArrayList<>();
        boolean allScoresFilled = true;
        int filledSetCount = 0;
        for (int i = 0; i < layoutMatchSetScoresContainer.getChildCount(); i++) {
            View setView = layoutMatchSetScoresContainer.getChildAt(i);
            EditText team1ScoreEt = setView.findViewById(R.id.editTextScoreTeam1);
            EditText team2ScoreEt = setView.findViewById(R.id.editTextScoreTeam2);
            String score1Str = team1ScoreEt.getText().toString();
            String score2Str = team2ScoreEt.getText().toString();
            if (!score1Str.isEmpty() && !score2Str.isEmpty()) {
                try {
                    int team1Score = Integer.parseInt(score1Str);
                    int team2Score = Integer.parseInt(score2Str);
                    setResults.add(new CreateMatchRequest.SetResult(i + 1, team1Score, team2Score));
                    filledSetCount++;
                } catch (NumberFormatException e) {
                    allScoresFilled = false;
                }
            } else {
                allScoresFilled = false;
            }
        }
        if (filledSetCount != numOfSets) {
            allScoresFilled = false;
        }
        if (Calendar.getInstance().getTime().before(matchDateTime)) {
            sendRequestToServer(new CreateMatchRequest(formatDate(matchDateTime), formatTime(matchDateTime), matchType, numOfSets, teams, new ArrayList<>()));
        } else if (allScoresFilled) {
            sendRequestToServer(new CreateMatchRequest(formatDate(matchDateTime), formatTime(matchDateTime), matchType, numOfSets, teams, setResults));
        } else {
            sendRequestToServer(new CreateMatchRequest(formatDate(matchDateTime), formatTime(matchDateTime), matchType, numOfSets, teams, setResults));
        }
    }

    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(date);
    }

    private String formatTime(Date date) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return timeFormat.format(date);
    }

    private void sendRequestToServer(CreateMatchRequest request) {
        if (getContext() == null) {
            Log.e("AddMatch_Fragment", "Context is null, cannot make API call.");
            return;
        }
        MatchService matchService = ApiClient.getClient(requireContext()).create(MatchService.class);
        matchService.createMatch(request).enqueue(new Callback<CreateMatchResponse>() {
            @Override
            public void onResponse(@NonNull Call<CreateMatchResponse> call, @NonNull Response<CreateMatchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(requireContext(), "Trận đấu đã được tạo thành công!", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
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
                }
            }
            @Override
            public void onFailure(@NonNull Call<CreateMatchResponse> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}