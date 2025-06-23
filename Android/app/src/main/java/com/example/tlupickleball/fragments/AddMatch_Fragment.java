package com.example.tlupickleball.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.example.tlupickleball.model.Match;
import com.example.tlupickleball.model.MatchSet;
import com.example.tlupickleball.model.Participant;
import com.example.tlupickleball.model.User;
import com.example.tlupickleball.network.api_model.match.CreateMatchRequest;
import com.example.tlupickleball.network.api_model.match.CreateMatchResponse;
import com.example.tlupickleball.network.api_model.match.UpdateScoresRequest;
import com.example.tlupickleball.network.core.ApiClient;
import com.example.tlupickleball.network.service.MatchService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMatch_Fragment extends Fragment implements PlayerSelectionDialog.OnPlayerSelectedInDialogListener {

    private ImageButton backButton;
    private TextView titleTextView;
    private Button inputTimeButton, selectDateButton, confirmButton, cancelMatchButton, confirmChangesButton;
    private TextView item1, item2;
    private View tabIndicator;
    private FrameLayout frameLayoutTabContainer;
    private Spinner setCountSpinner;
    private LinearLayout layoutMatchSetScoresContainer;
    private TextView player1Team1Name, player2Team2Name, player2Team1Name, player1Team2Name;
    private ImageButton player1Team1AddButton, player2Team2AddButton, player2Team1AddButton, player1Team2AddButton;
    private ImageView player1Team1Avatar, player2Team2Avatar, player2Team1Avatar, player1Team2Avatar;
    private User selectedPlayer1Team1, selectedPlayer2Team2, selectedPlayer2Team1, selectedPlayer1Team2;
    private Calendar selectedDateTime;
    private int selectedSetCount;
    private boolean isSinglesMatch = true;
    private int currentSelectedPlayerSlot = -1;

    private boolean isDetailMode = false;
    private String matchId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedDateTime = Calendar.getInstance();
        if (getArguments() != null) {
            matchId = getArguments().getString("match_id");
            if (matchId != null && !matchId.isEmpty()) {
                isDetailMode = true;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_match, container, false);
        initViews(view);
        setupListeners();
        if (isDetailMode) {
            titleTextView.setText("Chi tiết trận đấu");
            fetchMatchDetails();
        } else {
            titleTextView.setText("Ghi nhận trận đấu");
            configureUiForMode(false, null);
            updateDateButton(selectedDateTime.getTime());
            updateTimeButton(selectedDateTime.getTime());
            updateSetScoreViews(1, null);
            frameLayoutTabContainer.post(() -> selectTab(true));
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBackPress();
            }
        });
    }

    private void initViews(View view) {
        backButton = view.findViewById(R.id.backButton);
        titleTextView = view.findViewById(R.id.add_match_title);
        inputTimeButton = view.findViewById(R.id.inputTimeButton);
        selectDateButton = view.findViewById(R.id.selectDateButton);
        confirmButton = view.findViewById(R.id.confirmButton);
        cancelMatchButton = view.findViewById(R.id.cancelMatchButton);
        confirmChangesButton = view.findViewById(R.id.confirmChangesButton);
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
        backButton.setOnClickListener(v -> handleBackPress());
        cancelMatchButton.setOnClickListener(v -> showCancelConfirmationDialog());
        confirmChangesButton.setOnClickListener(v -> saveScoreChanges());

        if (!isDetailMode) {
            inputTimeButton.setOnClickListener(v -> showTimePicker());
            selectDateButton.setOnClickListener(v -> showDatePicker());
            confirmButton.setOnClickListener(v -> recordMatch());
            item1.setOnClickListener(v -> selectTab(true));
            item2.setOnClickListener(v -> selectTab(false));
            setCountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedSetCount = Integer.parseInt(parent.getItemAtPosition(position).toString());
                    updateSetScoreViews(selectedSetCount, null);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
            player1Team1AddButton.setOnClickListener(v -> { currentSelectedPlayerSlot = 0; showPlayerSelectionDialog(); });
            player2Team2AddButton.setOnClickListener(v -> { currentSelectedPlayerSlot = 1; showPlayerSelectionDialog(); });
            player2Team1AddButton.setOnClickListener(v -> { currentSelectedPlayerSlot = 2; showPlayerSelectionDialog(); });
            player1Team2AddButton.setOnClickListener(v -> { currentSelectedPlayerSlot = 3; showPlayerSelectionDialog(); });
        }
    }

    private void handleBackPress() {
        if (!isDetailMode && hasUnsavedData()) {
            showExitConfirmationDialog();
        } else {
            getParentFragmentManager().popBackStack();
        }
    }

    private void configureUiForMode(boolean forDetails, @Nullable Match match) {
        inputTimeButton.setEnabled(!forDetails);
        selectDateButton.setEnabled(!forDetails);
        setCountSpinner.setEnabled(!forDetails);
        item1.setClickable(!forDetails);
        item2.setClickable(!forDetails);

        int addPlayerButtonVisibility = forDetails ? View.INVISIBLE : View.VISIBLE;
        player1Team1AddButton.setVisibility(addPlayerButtonVisibility);
        player2Team1AddButton.setVisibility(addPlayerButtonVisibility);
        player1Team2AddButton.setVisibility(addPlayerButtonVisibility);
        player2Team2AddButton.setVisibility(addPlayerButtonVisibility);

        confirmButton.setVisibility(forDetails ? View.GONE : View.VISIBLE);
        confirmChangesButton.setVisibility(View.GONE);
        cancelMatchButton.setVisibility(View.GONE);

        boolean canUpdateOrCancel = false;
        if (forDetails && match != null) {
            String status = match.getStatus();
            canUpdateOrCancel = "ongoing".equalsIgnoreCase(status) || "in_progress".equalsIgnoreCase(status) || "upcoming".equalsIgnoreCase(status) || "pending".equalsIgnoreCase(status);

            if (canUpdateOrCancel) {
                confirmChangesButton.setVisibility(View.VISIBLE);
                cancelMatchButton.setVisibility(View.VISIBLE);
            }
        }

        for (int i = 0; i < layoutMatchSetScoresContainer.getChildCount(); i++) {
            View setView = layoutMatchSetScoresContainer.getChildAt(i);
            boolean isEditable = !forDetails || canUpdateOrCancel;
            setView.findViewById(R.id.editTextScoreTeam1).setEnabled(isEditable);
            setView.findViewById(R.id.editTextScoreTeam2).setEnabled(isEditable);
        }
    }

    private void fetchMatchDetails() {
        if (matchId == null || getContext() == null) return;
        MatchService service = ApiClient.getClient(getContext()).create(MatchService.class);
        service.getMatchDetail(matchId).enqueue(new Callback<Match>() {
            @Override
            public void onResponse(@NonNull Call<Match> call, @NonNull Response<Match> response) {
                if (response.isSuccessful() && response.body() != null) {
                    populateUiWithMatchDetails(response.body());
                } else {
                    String errorMsg = "Tải chi tiết thất bại: " + response.code();
                    if (response.errorBody() != null) {
                        try { errorMsg += " - " + response.errorBody().string(); } catch (Exception e) { /* ignore */ }
                    }
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<Match> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void populateUiWithMatchDetails(Match match) {
        configureUiForMode(true, match);

        SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        apiDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            if (match.getStartTime() != null) {
                Date matchDate = apiDateFormat.parse(match.getStartTime());
                updateDateButton(matchDate);
                updateTimeButton(matchDate);
            }
        } catch (ParseException e) {
            // Handle error
        }

        isSinglesMatch = "SINGLES".equalsIgnoreCase(match.getType());
        frameLayoutTabContainer.post(() -> selectTab(isSinglesMatch));

        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) setCountSpinner.getAdapter();
        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getItem(i).toString().equals(String.valueOf(match.getSetCount()))) {
                    setCountSpinner.setSelection(i, false);
                    updateSetScoreViews(match.getSetCount(), match.getSetResults());
                    break;
                }
            }
        } else {
            updateSetScoreViews(match.getSetCount(), match.getSetResults());
        }

        List<Participant> team1 = new ArrayList<>();
        List<Participant> team2 = new ArrayList<>();
        if (match.getParticipants() != null) {
            for (Participant p : match.getParticipants()) {
                if (p.getTeam() == 1) team1.add(p); else team2.add(p);
            }
        }
        if (!team1.isEmpty()) {
            player1Team1Name.setText(abbreviateName(team1.get(0).getFullName()));
            loadPlayerAvatar(team1.get(0).getAvatarUrl(), player1Team1Avatar);
            if (team1.size() > 1) {
                player2Team1Name.setText(abbreviateName(team1.get(1).getFullName()));
                loadPlayerAvatar(team1.get(1).getAvatarUrl(), player2Team1Avatar);
            }
        }
        if (!team2.isEmpty()) {
            player2Team2Name.setText(abbreviateName(team2.get(0).getFullName()));
            loadPlayerAvatar(team2.get(0).getAvatarUrl(), player2Team2Avatar);
            if (team2.size() > 1) {
                player1Team2Name.setText(abbreviateName(team2.get(1).getFullName()));
                loadPlayerAvatar(team2.get(1).getAvatarUrl(), player1Team2Avatar);
            }
        }
    }

    private void updateSetScoreViews(int setCount, List<MatchSet> results) {
        layoutMatchSetScoresContainer.removeAllViews();
        if (getContext() == null) return;

        boolean canEditScores = !isDetailMode;
        if(isDetailMode && (confirmChangesButton.getVisibility() == View.VISIBLE)) {
            canEditScores = true;
        }

        for (int i = 0; i < setCount; i++) {
            View setScoreView = LayoutInflater.from(getContext()).inflate(R.layout.item_match_set_score, layoutMatchSetScoresContainer, false);
            TextView setNumber = setScoreView.findViewById(R.id.textViewSetLabel);
            EditText team1ScoreEt = setScoreView.findViewById(R.id.editTextScoreTeam1);
            EditText team2ScoreEt = setScoreView.findViewById(R.id.editTextScoreTeam2);
            setNumber.setText(String.format(Locale.getDefault(), "Set %d", i + 1));
            if (results != null && i < results.size()) {
                MatchSet setResult = results.get(i);
                team1ScoreEt.setText(String.valueOf(setResult.getTeam1Score()));
                team2ScoreEt.setText(String.valueOf(setResult.getTeam2Score()));
            }
            team1ScoreEt.setEnabled(canEditScores);
            team2ScoreEt.setEnabled(canEditScores);
            layoutMatchSetScoresContainer.addView(setScoreView);
        }
    }

    private void saveScoreChanges() {
        if (getContext() == null || matchId == null) return;
        List<CreateMatchRequest.SetResult> setResults = new ArrayList<>();
        int numberOfSets = layoutMatchSetScoresContainer.getChildCount();

        for (int i = 0; i < numberOfSets; i++) {
            View setView = layoutMatchSetScoresContainer.getChildAt(i);
            EditText team1ScoreEt = setView.findViewById(R.id.editTextScoreTeam1);
            EditText team2ScoreEt = setView.findViewById(R.id.editTextScoreTeam2);
            try {
                int team1Score = Integer.parseInt(team1ScoreEt.getText().toString());
                int team2Score = Integer.parseInt(team2ScoreEt.getText().toString());
                setResults.add(new CreateMatchRequest.SetResult(i + 1, team1Score, team2Score));
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Vui lòng nhập điểm số hợp lệ cho tất cả các set.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (setResults.size() != numberOfSets) {
            Toast.makeText(getContext(), "Vui lòng điền đủ tỉ số cho tất cả các set.", Toast.LENGTH_SHORT).show();
            return;
        }

        UpdateScoresRequest request = new UpdateScoresRequest(setResults);
        MatchService service = ApiClient.getClient(getContext()).create(MatchService.class);
        service.updateMatchScores(matchId, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Cập nhật tỉ số thành công!", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getContext(), "Cập nhật thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCancelConfirmationDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận hủy trận")
                .setMessage("Bạn có chắc chắn muốn hủy trận đấu này? Hành động này không thể hoàn tác.")
                .setPositiveButton("Xác nhận", (dialog, which) -> deleteMatch())
                .setNegativeButton("Quay lại", null).show();
    }

    private void deleteMatch() {
        if (matchId == null || getContext() == null) return;
        MatchService service = ApiClient.getClient(getContext()).create(MatchService.class);
        service.deleteMatch(matchId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã hủy trận đấu thành công", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getContext(), "Hủy trận đấu thất bại", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean hasUnsavedData() {
        if (selectedPlayer1Team1 != null || selectedPlayer2Team1 != null || selectedPlayer1Team2 != null || selectedPlayer2Team2 != null) {
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

    private void showExitConfirmationDialog() {
        new AlertDialog.Builder(getContext())
                .setMessage("Trận đấu của bạn sẽ kết thúc\nDữ liệu chưa lưu sẽ bị mất.")
                .setPositiveButton("Xác nhận", (dialog, which) -> getParentFragmentManager().popBackStack())
                .setNegativeButton("Quay lại", null).show();
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
            Glide.with(this).load(player.getAvatarUrl()).circleCrop().into(imageView);
        } else {
        }
    }

    private void loadPlayerAvatar(String avatarUrl, ImageView imageView) {
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(this).load(avatarUrl).circleCrop().into(imageView);
        } else {
        }
    }

    private String abbreviateName(String fullName) { if (fullName == null || fullName.trim().isEmpty()) return ""; String[] parts = fullName.trim().split("\\s+"); if (parts.length > 0) { StringBuilder abbreviated = new StringBuilder(); for (int i = 0; i < parts.length - 1; i++) { if (!parts[i].isEmpty()) abbreviated.append(parts[i].charAt(0)).append("."); } abbreviated.append(parts[parts.length - 1]); return abbreviated.toString(); } return fullName; }
    private void showTimePicker() { int hour = selectedDateTime.get(Calendar.HOUR_OF_DAY); int minute = selectedDateTime.get(Calendar.MINUTE); new TimePickerDialog(requireContext(), (view, hourOfDay, minuteOfHour) -> { selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay); selectedDateTime.set(Calendar.MINUTE, minuteOfHour); updateTimeButton(selectedDateTime.getTime()); }, hour, minute, true).show(); }
    private void updateTimeButton(Date date) { SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault()); inputTimeButton.setText(sdf.format(date)); }
    private void showDatePicker() { int year = selectedDateTime.get(Calendar.YEAR); int month = selectedDateTime.get(Calendar.MONTH); int day = selectedDateTime.get(Calendar.DAY_OF_MONTH); new DatePickerDialog(requireContext(), (view, year1, monthOfYear, dayOfMonth) -> { selectedDateTime.set(year1, monthOfYear, dayOfMonth); updateDateButton(selectedDateTime.getTime()); }, year, month, day).show(); }
    private void updateDateButton(Date date) { SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()); selectDateButton.setText(sdf.format(date)); }
    private void selectTab(boolean isSingles) { this.isSinglesMatch = isSingles; if(getContext() == null) return; animateTabIndicator(isSingles); if (isSingles) { item1.setTextColor(ContextCompat.getColor(getContext(), R.color.white)); item2.setTextColor(ContextCompat.getColor(getContext(), R.color.black)); } else { item1.setTextColor(ContextCompat.getColor(getContext(), R.color.black)); item2.setTextColor(ContextCompat.getColor(getContext(), R.color.white)); } updateCourtView(); }
    private void animateTabIndicator(boolean isSingles) { if (frameLayoutTabContainer == null || tabIndicator == null) return; int totalWidth = frameLayoutTabContainer.getWidth(); if (totalWidth == 0) { frameLayoutTabContainer.post(() -> animateTabIndicator(isSingles)); return; } int tabWidth = totalWidth / 2; ViewGroup.LayoutParams params = tabIndicator.getLayoutParams(); params.width = tabWidth; tabIndicator.setLayoutParams(params); float targetX = isSingles ? 0f : tabWidth; tabIndicator.animate().translationX(targetX).setDuration(300).start(); }
    private void updateCourtView() { if (isSinglesMatch) { player2Team1Name.setVisibility(View.GONE); player1Team2Name.setVisibility(View.GONE); if (!isDetailMode) { player2Team1AddButton.setVisibility(View.GONE); player1Team2AddButton.setVisibility(View.GONE); } player2Team1Avatar.setVisibility(View.GONE); player1Team2Avatar.setVisibility(View.GONE); if (!isDetailMode) { player2Team1Name.setText(""); player1Team2Name.setText(""); selectedPlayer2Team1 = null; selectedPlayer1Team2 = null;} } else { player2Team1Name.setVisibility(View.VISIBLE); player1Team2Name.setVisibility(View.VISIBLE); if (!isDetailMode) { player2Team1AddButton.setVisibility(View.VISIBLE); player1Team2AddButton.setVisibility(View.VISIBLE); } player2Team1Avatar.setVisibility(View.VISIBLE); player1Team2Avatar.setVisibility(View.VISIBLE); } }
    private void showPlayerSelectionDialog() { PlayerSelectionDialog dialog = new PlayerSelectionDialog(); Bundle args = new Bundle(); args.putInt("playerSlot", currentSelectedPlayerSlot); dialog.setArguments(args); dialog.setOnPlayerSelectedInDialogListener(this); dialog.show(getParentFragmentManager(), "PlayerSelectionDialog"); }
    private void recordMatch() { Date matchDateTime = selectedDateTime.getTime(); String matchType = isSinglesMatch ? "SINGLES" : "DOUBLES"; int numOfSets = selectedSetCount; List<String> team1PlayerIds = new ArrayList<>(); List<String> team2PlayerIds = new ArrayList<>(); if (selectedPlayer1Team1 != null) team1PlayerIds.add(selectedPlayer1Team1.getUid()); if (!isSinglesMatch && selectedPlayer2Team1 != null) team1PlayerIds.add(selectedPlayer2Team1.getUid()); if (selectedPlayer2Team2 != null) team2PlayerIds.add(selectedPlayer2Team2.getUid()); if (!isSinglesMatch && selectedPlayer1Team2 != null) team2PlayerIds.add(selectedPlayer1Team2.getUid()); if (isSinglesMatch) { if (team1PlayerIds.size() != 1 || team2PlayerIds.size() != 1) { Toast.makeText(requireContext(), "Vui lòng chọn đủ 2 người chơi cho trận đấu đơn.", Toast.LENGTH_SHORT).show(); return; } } else { if (team1PlayerIds.size() != 2 || team2PlayerIds.size() != 2) { Toast.makeText(requireContext(), "Vui lòng chọn đủ 4 người chơi cho trận đấu đôi.", Toast.LENGTH_SHORT).show(); return; } } CreateMatchRequest.Teams teams = new CreateMatchRequest.Teams(team1PlayerIds, team2PlayerIds); List<CreateMatchRequest.SetResult> setResults = new ArrayList<>(); boolean allScoresFilled = true; int filledSetCount = 0; for (int i = 0; i < layoutMatchSetScoresContainer.getChildCount(); i++) { View setView = layoutMatchSetScoresContainer.getChildAt(i); EditText team1ScoreEt = setView.findViewById(R.id.editTextScoreTeam1); EditText team2ScoreEt = setView.findViewById(R.id.editTextScoreTeam2); String score1Str = team1ScoreEt.getText().toString(); String score2Str = team2ScoreEt.getText().toString(); if (!score1Str.isEmpty() && !score2Str.isEmpty()) { try { int team1Score = Integer.parseInt(score1Str); int team2Score = Integer.parseInt(score2Str); setResults.add(new CreateMatchRequest.SetResult(i + 1, team1Score, team2Score)); filledSetCount++; } catch (NumberFormatException e) { allScoresFilled = false; } } else { allScoresFilled = false; } } if (filledSetCount != numOfSets) { allScoresFilled = false; } CreateMatchRequest request; if (Calendar.getInstance().getTime().before(matchDateTime)) { request = new CreateMatchRequest(formatDate(matchDateTime), formatTime(matchDateTime), matchType, numOfSets, teams, new ArrayList<>()); } else if (allScoresFilled) { request = new CreateMatchRequest(formatDate(matchDateTime), formatTime(matchDateTime), matchType, numOfSets, teams, setResults); } else { request = new CreateMatchRequest(formatDate(matchDateTime), formatTime(matchDateTime), matchType, numOfSets, teams, setResults); } sendRequestToServer(request); }
    private String formatDate(Date date) { SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); return dateFormat.format(date); }
    private String formatTime(Date date) { SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault()); return timeFormat.format(date); }
    private void sendRequestToServer(CreateMatchRequest request) { if (getContext() == null) return; MatchService matchService = ApiClient.getClient(getContext()).create(MatchService.class); matchService.createMatch(request).enqueue(new Callback<CreateMatchResponse>() { @Override public void onResponse(@NonNull Call<CreateMatchResponse> call, @NonNull Response<CreateMatchResponse> response) { if (response.isSuccessful() && response.body() != null) { Toast.makeText(getContext(), "Trận đấu đã được tạo thành công!", Toast.LENGTH_SHORT).show(); getParentFragmentManager().popBackStack(); } else { Toast.makeText(getContext(), "Tạo trận đấu thất bại: " + response.code(), Toast.LENGTH_LONG).show(); } } @Override public void onFailure(@NonNull Call<CreateMatchResponse> call, @NonNull Throwable t) { Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_LONG).show(); } }); }
}