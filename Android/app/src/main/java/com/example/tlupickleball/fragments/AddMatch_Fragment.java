package com.example.tlupickleball.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.tlupickleball.R;
import com.example.tlupickleball.fragments.PlayerSelectionDialog; // Import dialog
import com.example.tlupickleball.model.User; // Import model User của bạn
import com.example.tlupickleball.network.api_model.match.CreateMatchRequest;
import com.example.tlupickleball.network.core.ApiClient;
import com.example.tlupickleball.network.service.MatchService;
import com.example.tlupickleball.network.api_model.match.CreateMatchResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMatch_Fragment extends Fragment implements PlayerSelectionDialog.PlayerSelectedListener { //

    private ImageButton backButton;
    private Button inputTimeButton;
    private Button selectDateButton;
    private TextView item1MatchType;
    private TextView item2MatchType;
    private View tabIndicator;
    private LinearLayout customTabLayoutContainer;

    private Spinner setCountSpinner;
    private FrameLayout pickleballCourtContainer;
    private LinearLayout layoutMatchSetScoresContainer;
    private Button confirmButton;

    private Calendar selectedDate;
    private String selectedTime;
    private String selectedMatchType;
    private int selectedSetCount;

    // Sử dụng đối tượng User thay vì chỉ UIDs
    private List<User> team1Players = new ArrayList<>();
    private List<User> team2Players = new ArrayList<>();

    private MatchService matchService;

    // Khai báo TextViews để hiển thị tên người chơi
    private TextView player1Team1NameTextView;
    private TextView player2Team1NameTextView;
    private TextView player1Team2NameTextView;
    private TextView player2Team2NameTextView;

    // Khai báo ImageButtons để chọn người chơi (các nút "+")
    private ImageButton player1Team1AddButton;
    private ImageButton player2Team1AddButton;
    private ImageButton player1Team2AddButton;
    private ImageButton player2Team2AddButton;

    // Hằng số cho các vị trí người chơi
    private static final int PLAYER_SLOT_1_TEAM_1 = 1;
    private static final int PLAYER_SLOT_2_TEAM_1 = 2;
    private static final int PLAYER_SLOT_1_TEAM_2 = 3;
    private static final int PLAYER_SLOT_2_TEAM_2 = 4;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_match, container, false);

        initViews(view);
        setupListeners();
        setupInitialData();

        matchService = ApiClient.getClient(getContext()).create(MatchService.class);

        return view;
    }

    private void initViews(View view) {
        backButton = view.findViewById(R.id.backButton);
        inputTimeButton = view.findViewById(R.id.inputTimeButton);
        selectDateButton = view.findViewById(R.id.selectDateButton);

        item1MatchType = view.findViewById(R.id.item1);
        item2MatchType = view.findViewById(R.id.item2);
        tabIndicator = view.findViewById(R.id.tab_indicator);
        customTabLayoutContainer = view.findViewById(R.id.customTabLayout);

        setCountSpinner = view.findViewById(R.id.setCountSpinner);
        pickleballCourtContainer = view.findViewById(R.id.view_pickleball_court_container);
        layoutMatchSetScoresContainer = view.findViewById(R.id.layoutMatchSetScoresContainer);
        confirmButton = view.findViewById(R.id.confirmButton);


        player1Team1AddButton = pickleballCourtContainer.findViewById(R.id.player1_team1_add_button);
        player2Team1AddButton = pickleballCourtContainer.findViewById(R.id.player2_team1_add_button);
        player1Team2AddButton = pickleballCourtContainer.findViewById(R.id.player1_team2_add_button);
        player2Team2AddButton = pickleballCourtContainer.findViewById(R.id.player2_team2_add_button);
    }

    private void setupListeners() {
        backButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStack();
            } else {
                requireActivity().finish();
            }
        });

        inputTimeButton.setOnClickListener(v -> showTimePickerDialog());
        selectDateButton.setOnClickListener(v -> showDatePickerDialog());

        item1MatchType.setOnClickListener(v -> selectMatchType("single"));
        item2MatchType.setOnClickListener(v -> selectMatchType("double"));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.set_count_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setCountSpinner.setAdapter(adapter);
        setCountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSetCount = Integer.parseInt(parent.getItemAtPosition(position).toString());
                generateSetScoreInputs(selectedSetCount);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì
            }
        });

        confirmButton.setOnClickListener(v -> createMatch());

        // Thêm listener cho các nút thêm người chơi để hiển thị PlayerSelectionDialog
        player1Team1AddButton.setOnClickListener(v -> showPlayerSelectionDialog(PLAYER_SLOT_1_TEAM_1));
        player2Team1AddButton.setOnClickListener(v -> showPlayerSelectionDialog(PLAYER_SLOT_2_TEAM_1));
        player1Team2AddButton.setOnClickListener(v -> showPlayerSelectionDialog(PLAYER_SLOT_1_TEAM_2));
        player2Team2AddButton.setOnClickListener(v -> showPlayerSelectionDialog(PLAYER_SLOT_2_TEAM_2));
    }

    private void setupInitialData() {
        selectedDate = Calendar.getInstance();
        updateDateButton();

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        selectedTime = timeFormat.format(selectedDate.getTime());
        inputTimeButton.setText(selectedTime);

        selectMatchType("single");

        selectedSetCount = Integer.parseInt(setCountSpinner.getItemAtPosition(0).toString());
        generateSetScoreInputs(selectedSetCount);

        updatePickleballCourtView(); // Gọi ban đầu để thiết lập hiển thị chính xác
    }

    private void selectMatchType(String type) {
        selectedMatchType = type;
        updateMatchTypeTabs(type);
        updatePickleballCourtView();

        // Xóa người chơi khi chuyển loại trận đấu để tránh các lựa chọn không hợp lệ
        team1Players.clear();
        team2Players.clear();

        // Đặt lại TextViews
        if (player1Team1NameTextView != null) player1Team1NameTextView.setText("Chọn người chơi A");
        if (player2Team1NameTextView != null) player2Team1NameTextView.setText("Chọn người chơi C");
        if (player1Team2NameTextView != null) player1Team2NameTextView.setText("Chọn người chơi B");
        if (player2Team2NameTextView != null) player2Team2NameTextView.setText("Chọn người chơi D");
    }

    private void updateMatchTypeTabs(String selectedType) {
        if (customTabLayoutContainer.getWidth() == 0) {
            customTabLayoutContainer.post(() -> updateMatchTypeTabs(selectedType));
            return;
        }

        FrameLayout tabFrameLayout = customTabLayoutContainer.findViewById(R.id.frame_layout_tab_container);
        if (tabFrameLayout == null) {
            Log.e("AddMatch_Fragment", "Không tìm thấy FrameLayout chứa tab indicator.");
            return;
        }

        int tabWidth = tabFrameLayout.getWidth() / 2;
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) tabIndicator.getLayoutParams();
        params.width = tabWidth;

        if (selectedType.equals("single")) {
            params.setMarginStart(0);
            tabIndicator.setLayoutParams(params);
            item1MatchType.setTextColor(Color.WHITE);
            item2MatchType.setTextColor(Color.BLACK);
        } else if (selectedType.equals("double")) {
            params.setMarginStart(tabWidth);
            tabIndicator.setLayoutParams(params);
            item1MatchType.setTextColor(Color.BLACK);
            item2MatchType.setTextColor(Color.WHITE);
        }
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateButton();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateDateButton() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        selectDateButton.setText(dateFormat.format(selectedDate.getTime()));
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    inputTimeButton.setText(selectedTime);
                },
                selectedDate.get(Calendar.HOUR_OF_DAY),
                selectedDate.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    // Phương thức để hiển thị dialog chọn người chơi
    private void showPlayerSelectionDialog(int playerSlot) {
        PlayerSelectionDialog dialog = PlayerSelectionDialog.newInstance(playerSlot);
        dialog.setPlayerSelectedListener(this); // Đặt fragment này làm listener
        dialog.show(getParentFragmentManager(), "PlayerSelectionDialog");
    }

    // Callback khi một người chơi được chọn từ dialog
    @Override
    public void onPlayerSelected(User player, int playerSlot) {
        // Kiểm tra trùng lặp người chơi (cùng một người chơi ở nhiều vị trí)
        if (team1Players.contains(player) || team2Players.contains(player)) {
            Toast.makeText(getContext(), "Người chơi này đã được chọn cho một đội khác.", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (playerSlot) {
            case PLAYER_SLOT_1_TEAM_1:
                if (!team1Players.isEmpty()) team1Players.set(0, player);
                else team1Players.add(player);
                if (player1Team1NameTextView != null) player1Team1NameTextView.setText(player.getFullName());
                break;
            case PLAYER_SLOT_2_TEAM_1:
                if (selectedMatchType.equals("single")) {
                    Toast.makeText(getContext(), "Không thể thêm người chơi thứ 2 cho đấu đơn.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (team1Players.size() > 1) team1Players.set(1, player);
                else team1Players.add(player);
                if (player2Team1NameTextView != null) player2Team1NameTextView.setText(player.getFullName());
                break;
            case PLAYER_SLOT_1_TEAM_2:
                if (!team2Players.isEmpty()) team2Players.set(0, player);
                else team2Players.add(player);
                if (player1Team2NameTextView != null) player1Team2NameTextView.setText(player.getFullName());
                break;
            case PLAYER_SLOT_2_TEAM_2:
                if (selectedMatchType.equals("single")) {
                    Toast.makeText(getContext(), "Không thể thêm người chơi thứ 2 cho đấu đơn.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (team2Players.size() > 1) team2Players.set(1, player);
                else team2Players.add(player);
                if (player2Team2NameTextView != null) player2Team2NameTextView.setText(player.getFullName());
                break;
        }
        updatePickleballCourtView(); // Cập nhật UI sau khi chọn
    }


    private void updatePickleballCourtView() {
        // Cập nhật hiển thị tên người chơi dựa trên người chơi đã chọn
        if (player1Team1NameTextView != null) {
            player1Team1NameTextView.setText(team1Players.isEmpty() ? "Chọn người chơi A" : team1Players.get(0).getFullName());
        }
        if (player1Team2NameTextView != null) {
            player1Team2NameTextView.setText(team2Players.isEmpty() ? "Chọn người chơi B" : team2Players.get(0).getFullName());
        }

        // Điều chỉnh hiển thị của người chơi thứ hai trong mỗi đội dựa trên loại trận đấu
        if (selectedMatchType.equals("single")) {
            if (player2Team1NameTextView != null) player2Team1NameTextView.setVisibility(View.GONE);
            if (player2Team2NameTextView != null) player2Team2NameTextView.setVisibility(View.GONE);

            // Ẩn nút thêm người chơi thứ hai
            if (player2Team1AddButton != null) player2Team1AddButton.setVisibility(View.GONE);
            if (player2Team2AddButton != null) player2Team2AddButton.setVisibility(View.GONE);

        } else if (selectedMatchType.equals("double")) {
            if (player2Team1NameTextView != null) {
                player2Team1NameTextView.setVisibility(View.VISIBLE);
                player2Team1NameTextView.setText(team1Players.size() > 1 ? team1Players.get(1).getFullName() : "Chọn người chơi C");
            }
            if (player2Team2NameTextView != null) {
                player2Team2NameTextView.setVisibility(View.VISIBLE);
                player2Team2NameTextView.setText(team2Players.size() > 1 ? team2Players.get(1).getFullName() : "Chọn người chơi D");
            }

            // Hiển thị nút thêm người chơi thứ hai
            if (player2Team1AddButton != null) player2Team1AddButton.setVisibility(View.VISIBLE);
            if (player2Team2AddButton != null) player2Team2AddButton.setVisibility(View.VISIBLE);
        }
    }

    private void generateSetScoreInputs(int setCount) {
        layoutMatchSetScoresContainer.removeAllViews();

        for (int i = 1; i <= setCount; i++) {
            LinearLayout setRowLayout = new LinearLayout(requireContext());
            setRowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            setRowLayout.setOrientation(LinearLayout.HORIZONTAL);
            setRowLayout.setPadding(0, 8, 0, 8);

            TextView setNumberTextView = new TextView(requireContext());
            setNumberTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.2f));
            setNumberTextView.setText(String.format(Locale.getDefault(), "Set %d", i));
            setNumberTextView.setTextSize(16f);
            setNumberTextView.setTextColor(getResources().getColor(android.R.color.black));
            setNumberTextView.setPadding(16,0,0,0);


            EditText team1ScoreEditText = new EditText(requireContext());
            LinearLayout.LayoutParams scoreParams = new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.4f);
            scoreParams.setMarginEnd(8);
            team1ScoreEditText.setLayoutParams(scoreParams);
            team1ScoreEditText.setHint("Điểm đội 1");
            team1ScoreEditText.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
            team1ScoreEditText.setTextSize(14f);
            team1ScoreEditText.setTag("team1_score_set_" + i);
            team1ScoreEditText.setPadding(16,16,16,16);


            EditText team2ScoreEditText = new EditText(requireContext());
            team2ScoreEditText.setLayoutParams(new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.4f));
            team2ScoreEditText.setHint("Điểm đội 2");
            team2ScoreEditText.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
            team2ScoreEditText.setTextSize(14f);
            team2ScoreEditText.setTag("team2_score_set_" + i);
            team2ScoreEditText.setPadding(16,16,16,16);


            setRowLayout.addView(setNumberTextView);
            setRowLayout.addView(team1ScoreEditText);
            setRowLayout.addView(team2ScoreEditText);

            layoutMatchSetScoresContainer.addView(setRowLayout);
        }
    }

    private void createMatch() {
        if (selectedDate == null || selectedTime == null || selectedMatchType == null || selectedSetCount == 0) {
            Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin trận đấu.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra tính hợp lệ của việc chọn người chơi dựa trên loại trận đấu
        if (selectedMatchType.equals("single")) {
            if (team1Players.size() != 1 || team2Players.size() != 1) {
                Toast.makeText(requireContext(), "Vui lòng chọn 1 người chơi cho mỗi đội trong đấu đơn.", Toast.LENGTH_SHORT).show();
                return;
            }
        } else if (selectedMatchType.equals("double")) {
            if (team1Players.size() != 2 || team2Players.size() != 2) {
                Toast.makeText(requireContext(), "Vui lòng chọn 2 người chơi cho mỗi đội trong đấu đôi.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        List<CreateMatchRequest.SetResult> setResults = new ArrayList<>();
        for (int i = 1; i <= selectedSetCount; i++) {
            EditText team1ScoreEditText = layoutMatchSetScoresContainer.findViewWithTag("team1_score_set_" + i);
            EditText team2ScoreEditText = layoutMatchSetScoresContainer.findViewWithTag("team2_score_set_" + i);

            if (team1ScoreEditText == null || team2ScoreEditText == null ||
                    team1ScoreEditText.getText().toString().isEmpty() ||
                    team2ScoreEditText.getText().toString().isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ điểm số cho tất cả các set.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int team1Score = Integer.parseInt(team1ScoreEditText.getText().toString());
                int team2Score = Integer.parseInt(team2ScoreEditText.getText().toString());
                setResults.add(new CreateMatchRequest.SetResult(i, team1Score, team2Score));
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Điểm số phải là số nguyên hợp lệ.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String startDate = apiDateFormat.format(selectedDate.getTime());

        // Trích xuất UIDs từ các đối tượng User đã chọn
        List<String> team1PlayerUids = new ArrayList<>();
        for (User player : team1Players) {
            team1PlayerUids.add(player.getUid());
        }

        List<String> team2PlayerUids = new ArrayList<>();
        for (User player : team2Players) {
            team2PlayerUids.add(player.getUid());
        }

        CreateMatchRequest.Teams teams = new CreateMatchRequest.Teams(team1PlayerUids, team2PlayerUids);

        CreateMatchRequest matchRequest = new CreateMatchRequest(
                startDate,
                selectedTime,
                selectedMatchType,
                selectedSetCount,
                teams,
                setResults
        );

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonRequest = gson.toJson(matchRequest);
        Log.d("AddMatch_Fragment", "Match Request JSON: " + jsonRequest);

        sendCreateMatchRequest(matchRequest);
    }

    private void sendCreateMatchRequest(CreateMatchRequest request) {
        Call<CreateMatchResponse> call = matchService.createMatch(request);

        call.enqueue(new Callback<CreateMatchResponse>() {
            @Override
            public void onResponse(Call<CreateMatchResponse> call, Response<CreateMatchResponse> response) {
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