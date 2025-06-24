package com.example.tlupickleball.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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

    private View contentContainer;
    private ProgressBar progressBar;

    private boolean isDetailMode = false;
    private String matchId;

    // Biến mới để lưu trạng thái tỉ số ban đầu
    private List<MatchSet> initialSetResults = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Dòng này đã bị comment lại trong file bạn gửi, tôi giữ nguyên
        // sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
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
            contentContainer.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            fetchMatchDetails();
        } else {
            contentContainer.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
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
        contentContainer = view.findViewById(R.id.contentContainer);
        progressBar = view.findViewById(R.id.progressBar);
    }

    // Phương thức navigateBack này là phiên bản cũ, tôi giữ lại theo file bạn gửi
    private void navigateBack(boolean refreshData) {
        if (!isAdded()) return;

        Bundle result = new Bundle();
        result.putBoolean("didReturnFromDetail", true);

        if (refreshData) {
            result.putBoolean("needsRefresh", true);
            if (selectedDateTime != null) {
                SimpleDateFormat ymdFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                result.putString("modifiedDateKey", ymdFormat.format(selectedDateTime.getTime()));
            }
        }
        getParentFragmentManager().setFragmentResult("requestKey", result);
        getParentFragmentManager().popBackStack();
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
        if (hasUnsavedData()) {
            showExitConfirmationDialog();
        } else {
            navigateBack(false);
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
            String score1Str = team1ScoreEt.getText().toString();
            String score2Str = team2ScoreEt.getText().toString();
            if (!score1Str.isEmpty() && !score2Str.isEmpty()) {
                try {
                    int team1Score = Integer.parseInt(score1Str);
                    int team2Score = Integer.parseInt(score2Str);
                    setResults.add(new CreateMatchRequest.SetResult(i + 1, team1Score, team2Score));
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Điểm số ở Set " + (i + 1) + " không hợp lệ.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            else if (!score1Str.isEmpty() || !score2Str.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng điền đủ cả hai điểm số cho Set " + (i + 1) + ".", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        UpdateScoresRequest request = new UpdateScoresRequest(setResults);
        MatchService service = ApiClient.getClient(getContext()).create(MatchService.class);
        service.updateMatchScores(matchId, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Cập nhật tỉ số thành công!", Toast.LENGTH_SHORT).show();
                    navigateBack(true);
                } else {
                    String errorMsg = "Cập nhật thất bại: " + response.code();
                    try {
                        if (response.errorBody() != null) { errorMsg += " - " + response.errorBody().string(); }
                    } catch (Exception e) { Log.e("SaveScoreChanges", "Error parsing error body", e); }
                    Log.e("SaveScoreChanges", errorMsg);
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("SaveScoreChanges", "Lỗi mạng", t);
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteMatch() {
        if (matchId == null || getContext() == null) return;
        MatchService service = ApiClient.getClient(getContext()).create(MatchService.class);
        service.deleteMatch(matchId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã hủy trận đấu thành công", Toast.LENGTH_SHORT).show();
                    navigateBack(true);
                } else {
                    String errorMsg = "Hủy trận đấu thất bại: " + response.code();
                    Log.e("DeleteMatch", errorMsg);
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("DeleteMatch", "Lỗi mạng", t);
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showExitConfirmationDialog() {
        if (getContext() == null) return;
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom_warning);
        Button btnConfirmExit = dialog.findViewById(R.id.btn_confirm_exit);
        Button btnCancelExit = dialog.findViewById(R.id.btn_cancel_exit);
        btnConfirmExit.setOnClickListener(v -> {
            dialog.dismiss();
            navigateBack(false);
        });
        btnCancelExit.setOnClickListener(v -> dialog.dismiss());
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();
    }

    private void sendRequestToServer(CreateMatchRequest request) {
        if (getContext() == null) return;
        MatchService matchService = ApiClient.getClient(getContext()).create(MatchService.class);
        matchService.createMatch(request).enqueue(new Callback<CreateMatchResponse>() {
            @Override
            public void onResponse(@NonNull Call<CreateMatchResponse> call, @NonNull Response<CreateMatchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Trận đấu đã được tạo thành công!", Toast.LENGTH_SHORT).show();
                    navigateBack(true);
                } else {
                    Toast.makeText(getContext(), "Tạo trận đấu thất bại: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<CreateMatchResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_LONG).show();
            }
        });
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
        if (matchId == null || getContext() == null) {
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            if (contentContainer != null) contentContainer.setVisibility(View.VISIBLE);
            return;
        }
        MatchService service = ApiClient.getClient(getContext()).create(MatchService.class);
        service.getMatchDetail(matchId).enqueue(new Callback<Match>() {
            @Override
            public void onResponse(@NonNull Call<Match> call, @NonNull Response<Match> response) {
                progressBar.setVisibility(View.GONE);
                contentContainer.setVisibility(View.VISIBLE);
                if (response.isSuccessful() && response.body() != null) {
                    populateUiWithMatchDetails(response.body());
                } else {
                    Toast.makeText(getContext(), "Tải chi tiết thất bại: " + response.code(), Toast.LENGTH_LONG).show();
                    if (isAdded()) getParentFragmentManager().popBackStack();
                }
            }
            @Override
            public void onFailure(@NonNull Call<Match> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                contentContainer.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
                if (isAdded()) getParentFragmentManager().popBackStack();
            }
        });
    }

    private void populateUiWithMatchDetails(Match match) {
        configureUiForMode(true, match);

        // Lưu lại trạng thái tỉ số ban đầu khi dữ liệu được tải
        if (match.getSetResults() != null) {
            this.initialSetResults.clear();
            for (MatchSet set : match.getSetResults()) {
                this.initialSetResults.add(new MatchSet(set.getSetNumber(), set.getTeam1Score(), set.getTeam2Score()));
            }
        } else {
            this.initialSetResults.clear();
        }

        SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        try {
            if (match.getStartTime() != null) {
                Date matchDate = apiDateFormat.parse(match.getStartTime());
                selectedDateTime.setTime(matchDate);
                updateDateButton(matchDate);
                updateTimeButton(matchDate);
            }
        } catch (ParseException e) {
            android.util.Log.e("AddMatch_Fragment", "Error parsing date", e);
        }
        if (match.getParticipants() != null && match.getParticipants().size() > 2) {
            isSinglesMatch = false;
        } else {
            isSinglesMatch = true;
        }
        frameLayoutTabContainer.post(() -> selectTab(isSinglesMatch));
        int totalSetCount = match.getSetCount();
        if (totalSetCount <= 0) {
            android.util.Log.w("AddMatch_Fragment", "API did not return a valid setCount for the match.");
        }
        updateSetScoreViews(totalSetCount, match.getSetResults());
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) setCountSpinner.getAdapter();
        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getItem(i).toString().equals(String.valueOf(totalSetCount))) {
                    setCountSpinner.setSelection(i, false);
                    break;
                }
            }
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
            if (isSinglesMatch) {
                player1Team2Name.setText(abbreviateName(team2.get(0).getFullName()));
                loadPlayerAvatar(team2.get(0).getAvatarUrl(), player1Team2Avatar);
            } else {
                player2Team2Name.setText(abbreviateName(team2.get(0).getFullName()));
                loadPlayerAvatar(team2.get(0).getAvatarUrl(), player2Team2Avatar);
                if (team2.size() > 1) {
                    player1Team2Name.setText(abbreviateName(team2.get(1).getFullName()));
                    loadPlayerAvatar(team2.get(1).getAvatarUrl(), player1Team2Avatar);
                }
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

    private void showCancelConfirmationDialog() {
        if (getContext() == null) {
            return;
        }
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_match_delete_confirmation);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        btnConfirm.setOnClickListener(v -> {
            deleteMatch();
            dialog.dismiss();
        });
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();
    }

    private boolean hasUnsavedData() {
        // Đối với chế độ thêm mới, chỉ cần kiểm tra có người chơi được chọn không là đủ
        if (!isDetailMode) {
            return selectedPlayer1Team1 != null || selectedPlayer2Team1 != null || selectedPlayer1Team2 != null || selectedPlayer2Team2 != null;
        }

        // Đối với chế độ chi tiết, so sánh tỉ số hiện tại với tỉ số ban đầu
        if (layoutMatchSetScoresContainer != null) {
            int numSetViews = layoutMatchSetScoresContainer.getChildCount();

            for (int i = 0; i < numSetViews; i++) {
                View setView = layoutMatchSetScoresContainer.getChildAt(i);
                EditText team1ScoreEt = setView.findViewById(R.id.editTextScoreTeam1);
                EditText team2ScoreEt = setView.findViewById(R.id.editTextScoreTeam2);

                String currentScore1Str = team1ScoreEt.getText().toString();
                String currentScore2Str = team2ScoreEt.getText().toString();

                // Lấy tỉ số ban đầu cho set này
                int initialScore1 = -1; // -1 đại diện cho "chưa có điểm"
                int initialScore2 = -1;
                if (i < initialSetResults.size()) {
                    MatchSet initialSet = initialSetResults.get(i);
                    if (initialSet != null) {
                        initialScore1 = initialSet.getTeam1Score();
                        initialScore2 = initialSet.getTeam2Score();
                    }
                }

                // Lấy tỉ số hiện tại, nếu rỗng cũng coi là -1
                int currentScore1 = -1;
                int currentScore2 = -1;
                try {
                    if (!currentScore1Str.isEmpty()) {
                        currentScore1 = Integer.parseInt(currentScore1Str);
                    }
                    if (!currentScore2Str.isEmpty()) {
                        currentScore2 = Integer.parseInt(currentScore2Str);
                    }
                } catch (NumberFormatException e) {
                    return true; // Nếu người dùng nhập chữ -> coi như là thay đổi
                }

                // So sánh, nếu khác nhau -> có thay đổi
                if (currentScore1 != initialScore1 || currentScore2 != initialScore2) {
                    return true;
                }
            }
        }
        return false;
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
        } else {}
    }

    private void loadPlayerAvatar(String avatarUrl, ImageView imageView) {
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(this).load(avatarUrl).circleCrop().into(imageView);
        } else {}
    }

    private String abbreviateName(String fullName) { if (fullName == null || fullName.trim().isEmpty()) return ""; String[] parts = fullName.trim().split("\\s+"); if (parts.length > 0) { StringBuilder abbreviated = new StringBuilder(); for (int i = 0; i < parts.length - 1; i++) { if (!parts[i].isEmpty()) abbreviated.append(parts[i].charAt(0)).append("."); } abbreviated.append(parts[parts.length - 1]); return abbreviated.toString(); } return fullName; }
    private void showTimePicker() { int hour = selectedDateTime.get(Calendar.HOUR_OF_DAY); int minute = selectedDateTime.get(Calendar.MINUTE); new TimePickerDialog(requireContext(), (view, hourOfDay, minuteOfHour) -> { selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay); selectedDateTime.set(Calendar.MINUTE, minuteOfHour); updateTimeButton(selectedDateTime.getTime()); }, hour, minute, true).show(); }
    private void updateTimeButton(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        inputTimeButton.setText(sdf.format(date));
    }

    private void showDatePicker() { int year = selectedDateTime.get(Calendar.YEAR); int month = selectedDateTime.get(Calendar.MONTH); int day = selectedDateTime.get(Calendar.DAY_OF_MONTH); new DatePickerDialog(requireContext(), (view, year1, monthOfYear, dayOfMonth) -> { selectedDateTime.set(year1, monthOfYear, dayOfMonth); updateDateButton(selectedDateTime.getTime()); }, year, month, day).show(); }
    private void updateDateButton(Date date) { SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()); selectDateButton.setText(sdf.format(date)); }
    private void selectTab(boolean isSingles) { this.isSinglesMatch = isSingles; if(getContext() == null) return; animateTabIndicator(isSingles); if (isSingles) { item1.setTextColor(ContextCompat.getColor(getContext(), R.color.white)); item2.setTextColor(ContextCompat.getColor(getContext(), R.color.black)); } else { item1.setTextColor(ContextCompat.getColor(getContext(), R.color.black)); item2.setTextColor(ContextCompat.getColor(getContext(), R.color.white)); } updateCourtView(); }
    private void animateTabIndicator(boolean isSingles) { if (frameLayoutTabContainer == null || tabIndicator == null) return; int totalWidth = frameLayoutTabContainer.getWidth(); if (totalWidth == 0) { frameLayoutTabContainer.post(() -> animateTabIndicator(isSingles)); return; } int tabWidth = totalWidth / 2; ViewGroup.LayoutParams params = tabIndicator.getLayoutParams(); params.width = tabWidth; tabIndicator.setLayoutParams(params); float targetX = isSingles ? 0f : tabWidth; tabIndicator.animate().translationX(targetX).setDuration(300).start(); }
    private void updateCourtView() {
        if (!isAdded()) {
            return;
        }

        if (isSinglesMatch) {
            player2Team2Name.setVisibility(View.GONE);
            player2Team2Avatar.setVisibility(View.GONE);
            player2Team1Name.setVisibility(View.GONE);
            player2Team1Avatar.setVisibility(View.GONE);
            player1Team1Name.setVisibility(View.VISIBLE);
            player1Team1Avatar.setVisibility(View.VISIBLE);
            player1Team2Name.setVisibility(View.VISIBLE);
            player1Team2Avatar.setVisibility(View.VISIBLE);
            if (!isDetailMode) {
                player2Team2AddButton.setVisibility(View.GONE);
                player2Team1AddButton.setVisibility(View.GONE);
                player1Team1AddButton.setVisibility(View.VISIBLE);
                player1Team2AddButton.setVisibility(View.VISIBLE);
            }

        } else {
            player1Team1Name.setVisibility(View.VISIBLE);
            player1Team1Avatar.setVisibility(View.VISIBLE);
            player2Team1Name.setVisibility(View.VISIBLE);
            player2Team1Avatar.setVisibility(View.VISIBLE);
            player2Team2Name.setVisibility(View.VISIBLE);
            player2Team2Avatar.setVisibility(View.VISIBLE);
            player1Team2Name.setVisibility(View.VISIBLE);
            player1Team2Avatar.setVisibility(View.VISIBLE);
            if (!isDetailMode) {
                player1Team1AddButton.setVisibility(View.VISIBLE);
                player2Team1AddButton.setVisibility(View.VISIBLE);
                player2Team2AddButton.setVisibility(View.VISIBLE);
                player1Team2AddButton.setVisibility(View.VISIBLE);
            }
        }
    }
    private void showPlayerSelectionDialog() {
        PlayerSelectionDialog dialog = new PlayerSelectionDialog();
        Bundle args = new Bundle();
        args.putInt("playerSlot", currentSelectedPlayerSlot);
        ArrayList<String> excludedPlayerIds = new ArrayList<>();
        if (selectedPlayer1Team1 != null) excludedPlayerIds.add(selectedPlayer1Team1.getUid());
        if (selectedPlayer2Team1 != null) excludedPlayerIds.add(selectedPlayer2Team1.getUid());
        if (selectedPlayer1Team2 != null) excludedPlayerIds.add(selectedPlayer1Team2.getUid());
        if (selectedPlayer2Team2 != null) excludedPlayerIds.add(selectedPlayer2Team2.getUid());
        args.putStringArrayList("excludedPlayerIds", excludedPlayerIds);
        dialog.setArguments(args);
        dialog.setOnPlayerSelectedInDialogListener(this);
        dialog.show(getParentFragmentManager(), "PlayerSelectionDialog");
    }
    private void recordMatch() {
        Date matchDateTime = selectedDateTime.getTime();
        String matchType = isSinglesMatch ? "single" : "double";
        int numOfSets = selectedSetCount;
        List<String> team1PlayerIds = new ArrayList<>();
        List<String> team2PlayerIds = new ArrayList<>();
        if (selectedPlayer1Team1 != null) {
            team1PlayerIds.add(selectedPlayer1Team1.getUid());
        }
        if (!isSinglesMatch && selectedPlayer2Team1 != null) {
            team1PlayerIds.add(selectedPlayer2Team1.getUid());
        }
        if (isSinglesMatch) {
            if (selectedPlayer1Team2 != null) {
                team2PlayerIds.add(selectedPlayer1Team2.getUid());
            }
        } else {
            if (selectedPlayer2Team2 != null) {
                team2PlayerIds.add(selectedPlayer2Team2.getUid());
            }
            if (selectedPlayer1Team2 != null) {
                team2PlayerIds.add(selectedPlayer1Team2.getUid());
            }
        }
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
        CreateMatchRequest request;
        if (Calendar.getInstance().getTime().before(matchDateTime)) {
            request = new CreateMatchRequest(formatDate(matchDateTime), formatTime(matchDateTime), matchType, numOfSets, teams, new ArrayList<>());
        } else if (allScoresFilled) {
            request = new CreateMatchRequest(formatDate(matchDateTime), formatTime(matchDateTime), matchType, numOfSets, teams, setResults);
        } else {
            request = new CreateMatchRequest(formatDate(matchDateTime), formatTime(matchDateTime), matchType, numOfSets, teams, setResults);
        }
        sendRequestToServer(request);
    }
    private String formatDate(Date date) { SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); return dateFormat.format(date); }
    private String formatTime(Date date) { SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault()); return timeFormat.format(date); }
}