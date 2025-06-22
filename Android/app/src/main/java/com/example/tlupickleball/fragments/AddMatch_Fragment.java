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
import android.widget.ImageButton; // Import ImageButton
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
import com.example.tlupickleball.network.api_model.match.CreateMatchRequest;
import com.example.tlupickleball.network.core.ApiClient;
import com.example.tlupickleball.network.service.MatchService;
import com.example.tlupickleball.network.api_model.match.CreateMatchResponse; // Import CreateMatchResponse

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMatch_Fragment extends Fragment {

    private ImageButton backButton;
    private Button inputTimeButton;
    private Button selectDateButton;
    private TextView item1MatchType; // Đấu đơn
    private TextView item2MatchType; // Đấu đôi
    private View tabIndicator; // View di chuyển để chỉ báo tab
    private LinearLayout customTabLayoutContainer; // Đã sửa từ FrameLayout sang LinearLayout

    private Spinner setCountSpinner;
    private FrameLayout pickleballCourtContainer;
    private LinearLayout layoutMatchSetScoresContainer;
    private Button confirmButton;

    private Calendar selectedDate;
    private String selectedTime;
    private String selectedMatchType; // "single" hoặc "double"
    private int selectedSetCount;

    private List<String> team1Players = new ArrayList<>();
    private List<String> team2Players = new ArrayList<>();

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

        // Ánh xạ các ImageButton để thêm người chơi
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

        // Thêm listener cho các nút thêm người chơi (tùy chọn)
        player1Team1AddButton.setOnClickListener(v -> Toast.makeText(getContext(), "Chọn người chơi A", Toast.LENGTH_SHORT).show());
        player2Team1AddButton.setOnClickListener(v -> Toast.makeText(getContext(), "Chọn người chơi C", Toast.LENGTH_SHORT).show());
        player1Team2AddButton.setOnClickListener(v -> Toast.makeText(getContext(), "Chọn người chơi B", Toast.LENGTH_SHORT).show());
        player2Team2AddButton.setOnClickListener(v -> Toast.makeText(getContext(), "Chọn người chơi D", Toast.LENGTH_SHORT).show());
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

        // UUID người chơi giả để minh họa
        team1Players.add("7e008907-f932-4857-b9a6-fec32c30fe93");
        team2Players.add("e8fd3d9e-431c-492e-8cb6-f7b3728e5341");
        updatePickleballCourtView();
    }

    private void selectMatchType(String type) {
        selectedMatchType = type;
        updateMatchTypeTabs(type);
        updatePickleballCourtView();

        if (selectedMatchType.equals("single")) {
            if (team1Players.size() > 1) team1Players = team1Players.subList(0,1);
            if (team2Players.size() > 1) team2Players = team2Players.subList(0,1);
        } else { // "double"
            // Đảm bảo có đủ 2 người chơi mỗi đội cho đấu đôi
            while (team1Players.size() < 2) team1Players.add(UUID.randomUUID().toString());
            while (team2Players.size() < 2) team2Players.add(UUID.randomUUID().toString());
        }
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

    private void updatePickleballCourtView() {
        // Cập nhật hiển thị tên người chơi
        if (player1Team1NameTextView != null) player1Team1NameTextView.setText("Người chơi A");
        if (player1Team2NameTextView != null) player1Team2NameTextView.setText("Người chơi B");

        // Điều chỉnh hiển thị của người chơi thứ 2 trong mỗi đội tùy thuộc vào loại trận đấu
        if (selectedMatchType.equals("single")) {
            if (player2Team1NameTextView != null) player2Team1NameTextView.setVisibility(View.GONE);
            if (player2Team2NameTextView != null) player2Team2NameTextView.setVisibility(View.GONE);

            // Ẩn các nút thêm người chơi thứ 2
            if (player2Team1AddButton != null) player2Team1AddButton.setVisibility(View.GONE);
            if (player2Team2AddButton != null) player2Team2AddButton.setVisibility(View.GONE);

        } else if (selectedMatchType.equals("double")) {
            if (player2Team1NameTextView != null) {
                player2Team1NameTextView.setVisibility(View.VISIBLE);
                player2Team1NameTextView.setText("Người chơi C");
            }
            if (player2Team2NameTextView != null) {
                player2Team2NameTextView.setVisibility(View.VISIBLE);
                player2Team2NameTextView.setText("Người chơi D");
            }

            // Hiển thị các nút thêm người chơi thứ 2
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

        // Logic để đảm bảo đủ người chơi cho loại trận đấu đã chọn
        // Nếu là đấu đơn, chỉ giữ lại 1 người chơi mỗi đội.
        // Nếu là đấu đôi, đảm bảo có 2 người chơi mỗi đội (thêm UUID giả nếu thiếu).
        if (selectedMatchType.equals("single")) {
            if (team1Players.size() > 1) team1Players = team1Players.subList(0, 1);
            else if (team1Players.isEmpty()) team1Players.add(UUID.randomUUID().toString()); // Đảm bảo có ít nhất 1
            if (team2Players.size() > 1) team2Players = team2Players.subList(0, 1);
            else if (team2Players.isEmpty()) team2Players.add(UUID.randomUUID().toString()); // Đảm bảo có ít nhất 1
        } else if (selectedMatchType.equals("double")) {
            while (team1Players.size() < 2) team1Players.add(UUID.randomUUID().toString());
            while (team2Players.size() < 2) team2Players.add(UUID.randomUUID().toString());
        }


        CreateMatchRequest.Teams teams = new CreateMatchRequest.Teams(team1Players, team2Players);

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