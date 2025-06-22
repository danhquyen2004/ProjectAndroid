package com.example.tlupickleball.fragments;

import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.app.Dialog; // Đảm bảo import Dialog
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable; // Đảm bảo import ColorDrawable
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity; // Đảm bảo import Gravity
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window; // Đảm bảo import Window
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText; // Đảm bảo import EditText
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback; // Đảm bảo import OnBackPressedCallback
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tlupickleball.R;
import com.example.tlupickleball.data.MatchDataManager;
import com.example.tlupickleball.data.MockMatchDataManager;
import com.example.tlupickleball.model.Matches;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddMatch_Fragment extends Fragment {

    private ImageButton backButton;
    private Button inputTimeButton;
    private Button selectDateButton;
    private Spinner setCountSpinner;
    private Button confirmButton;

    private ImageButton slot1, slot2, slot3, slot4;
    private TextView id_txtslot1, id_txtslot2, id_txtslot3, id_txtslot4;

    private TextView item1, item2; // item1 và item2 là các TextView dùng cho tab
    private View tabIndicator; // tabIndicator là View di chuyển

    private String currentMatchFormat = "double";

    private MatchDataManager matchDataManager;

    private Calendar selectedDateTime = Calendar.getInstance();
    private String selectedSetCount = "1 set";

    // Giá trị mặc định cho người chơi, bạn có thể thay đổi hoặc tải từ nguồn dữ liệu
    private String player1Name = "Ảnh";
    private String player2Name = "Đồng";
    private String player3Name = "Hữu";
    private String player4Name = "Quyền";

    private LinearLayout layoutMatchSetScores; // Container cho các trường nhập điểm số

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_match, container, false);

        matchDataManager = new MockMatchDataManager(); // Khởi tạo MatchDataManager

        // Ánh xạ các View từ layout
        backButton = view.findViewById(R.id.backButton);
        inputTimeButton = view.findViewById(R.id.inputTimeButton);
        selectDateButton = view.findViewById(R.id.selectDateButton);
        setCountSpinner = view.findViewById(R.id.setCountSpinner);
        confirmButton = view.findViewById(R.id.confirmButton);

        slot1 = view.findViewById(R.id.slot1);
        slot2 = view.findViewById(R.id.slot2);
        slot3 = view.findViewById(R.id.slot3);
        slot4 = view.findViewById(R.id.slot4);

        id_txtslot1 = view.findViewById(R.id.id_txtslot1);
        id_txtslot2 = view.findViewById(R.id.id_txtslot2);
        id_txtslot3 = view.findViewById(R.id.id_txtslot3);
        id_txtslot4 = view.findViewById(R.id.id_txtslot4);

        item1 = view.findViewById(R.id.item1); // Tab "Đấu đơn"
        item2 = view.findViewById(R.id.item2); // Tab "Đấu đôi"
        tabIndicator = view.findViewById(R.id.tab_indicator); // View chỉ báo tab được chọn

        layoutMatchSetScores = view.findViewById(R.id.layoutMatchSetScoresContainer);

        // Thiết lập các Listener và trạng thái ban đầu
        setupListeners();
        setupSpinner();
        updateDateTimeDisplay();
        updatePlayerSlotsVisibility();

        // Cập nhật số lượng ô nhập điểm dựa trên lựa chọn spinner ban đầu
        updateSetScoreInputs(Integer.parseInt(selectedSetCount.split(" ")[0]));

        // Đặt trạng thái ban đầu của tab sau khi các view được đo lường
        // Đảm bảo selectTab được gọi sau khi layout đã được vẽ hoàn chỉnh
        view.post(() -> {
            selectTab(currentMatchFormat);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Xử lý nút back của thiết bị hoặc cử chỉ back
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (hasUnsavedData()) {
                    // Nếu có dữ liệu chưa lưu, hiển thị dialog cảnh báo
                    showDiscardChangesDialog();
                } else {
                    // Nếu không có dữ liệu chưa lưu, vô hiệu hóa callback này
                    // để cho phép hành vi nút back mặc định (thoát khỏi fragment)
                    setEnabled(false);
                    requireActivity().onBackPressed(); // Thực hiện hành động back mặc định
                }
            }
        });
    }

    private void setupListeners() {
        // Xử lý nút back trên ActionBar/Toolbar tùy chỉnh
        backButton.setOnClickListener(v -> {
            if (hasUnsavedData()) {
                showDiscardChangesDialog();
            } else {
                // Nếu không có dữ liệu, chỉ cần gọi hành động back mặc định
                requireActivity().onBackPressed();
            }
        });

        // Xử lý chọn thời gian và ngày
        inputTimeButton.setOnClickListener(v -> showTimePickerDialog());
        selectDateButton.setOnClickListener(v -> showDatePickerDialog());

        // Xử lý nút xác nhận
        confirmButton.setOnClickListener(v -> confirmMatch());

        // Xử lý chuyển đổi giữa "Đấu đơn" và "Đấu đôi"
        item1.setOnClickListener(v -> selectTab("single"));
        item2.setOnClickListener(v -> selectTab("double"));

        // Xử lý chọn người chơi cho các vị trí
        slot1.setOnClickListener(v -> {
            // Thay đổi tên người chơi và cập nhật UI (ví dụ: mở dialog chọn người chơi)
            player1Name = "Nguyễn Văn A"; // Ví dụ: bạn sẽ thay bằng logic chọn người chơi thực tế
            updatePlayerSlotUI(slot1, id_txtslot1, player1Name);
            Toast.makeText(getContext(), "Đã chọn " + player1Name, Toast.LENGTH_SHORT).show();
        });
        slot2.setOnClickListener(v -> {
            player2Name = "Trần Thị B";
            updatePlayerSlotUI(slot2, id_txtslot2, player2Name);
            Toast.makeText(getContext(), "Đã chọn " + player2Name, Toast.LENGTH_SHORT).show();
        });
        slot3.setOnClickListener(v -> {
            player3Name = "Lê Văn C";
            updatePlayerSlotUI(slot3, id_txtslot3, player3Name);
            Toast.makeText(getContext(), "Đã chọn " + player3Name, Toast.LENGTH_SHORT).show();
        });
        slot4.setOnClickListener(v -> {
            player4Name = "Phạm Thị D";
            updatePlayerSlotUI(slot4, id_txtslot4, player4Name);
            Toast.makeText(getContext(), "Đã chọn " + player4Name, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupSpinner() {
        // Thiết lập Adapter cho Spinner chọn số set đấu
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.set_count_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setCountSpinner.setAdapter(adapter);

        // Lắng nghe sự kiện chọn item trên Spinner
        setCountSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                selectedSetCount = parent.getItemAtPosition(position).toString();
                int numberOfSets = Integer.parseInt(selectedSetCount.split(" ")[0]);
                updateSetScoreInputs(numberOfSets); // Cập nhật số ô nhập điểm
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void showTimePickerDialog() {
        int hour = selectedDateTime.get(Calendar.HOUR_OF_DAY);
        int minute = selectedDateTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, hourOfDay, minuteOfHour) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDateTime.set(Calendar.MINUTE, minuteOfHour);
                    updateDateTimeDisplay(); // Cập nhật hiển thị thời gian
                }, hour, minute, true); // true để hiển thị định dạng 24 giờ
        timePickerDialog.show();
    }

    private void showDatePickerDialog() {
        int year = selectedDateTime.get(Calendar.YEAR);
        int month = selectedDateTime.get(Calendar.MONTH);
        int day = selectedDateTime.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year1, month1, dayOfMonth) -> {
                    selectedDateTime.set(year1, month1, dayOfMonth);
                    updateDateTimeDisplay(); // Cập nhật hiển thị ngày
                }, year, month, day);
        datePickerDialog.show();
    }

    private void updateDateTimeDisplay() {
        // Định dạng và hiển thị ngày tháng, thời gian
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        selectDateButton.setText(dateFormat.format(selectedDateTime.getTime()));
        inputTimeButton.setText(timeFormat.format(selectedDateTime.getTime()));
    }

    private void selectTab(String format) {
        currentMatchFormat = format;

        // Đảm bảo các view đã được đo lường trước khi lấy chiều rộng
        // Nếu width là 0, tức là view chưa được vẽ, post lại để gọi sau
        if (item1.getWidth() == 0 || item2.getWidth() == 0) {
            item1.post(() -> selectTab(format));
            return;
        }

        int tabWidth = item1.getWidth(); // Cả hai tab nên có cùng chiều rộng
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) tabIndicator.getLayoutParams();

        if (currentMatchFormat.equals("single")) {
            // Di chuyển indicator đến vị trí "Đấu đơn"
            ObjectAnimator.ofFloat(tabIndicator, "translationX", 0f).setDuration(200).start();
            params.width = tabWidth; // Đặt chiều rộng của indicator bằng chiều rộng của tab
            tabIndicator.setLayoutParams(params);

            // Đặt màu chữ cho tab được chọn và không được chọn
            item1.setTextColor(Color.WHITE);
            item2.setTextColor(getResources().getColor(R.color.black));
        } else { // "double"
            // Di chuyển indicator đến vị trí "Đấu đôi"
            ObjectAnimator.ofFloat(tabIndicator, "translationX", tabWidth).setDuration(200).start();
            params.width = tabWidth; // Đặt chiều rộng của indicator bằng chiều rộng của tab
            tabIndicator.setLayoutParams(params);

            // Đặt màu chữ cho tab được chọn và không được chọn
            item1.setTextColor(getResources().getColor(R.color.black));
            item2.setTextColor(Color.WHITE);
        }
        updatePlayerSlotsVisibility(); // Cập nhật hiển thị slot người chơi
    }

    private void updatePlayerSlotsVisibility() {
        if (currentMatchFormat.equals("single")) {
            // Ẩn slot 3 và 4 nếu là đấu đơn
            if (slot3 != null) slot3.setVisibility(View.GONE);
            if (id_txtslot3 != null) {
                id_txtslot3.setVisibility(View.GONE);
                id_txtslot3.setText(""); // Xóa tên người chơi nếu bị ẩn
            }
            if (slot4 != null) slot4.setVisibility(View.GONE);
            if (id_txtslot4 != null) {
                id_txtslot4.setVisibility(View.GONE);
                id_txtslot4.setText(""); // Xóa tên người chơi nếu bị ẩn
            }
        } else {
            // Hiển thị slot 3 và 4 nếu là đấu đôi
            if (slot3 != null) slot3.setVisibility(View.VISIBLE);
            if (id_txtslot3 != null) {
                id_txtslot3.setVisibility(View.VISIBLE);
                id_txtslot3.setText(player3Name);
            }
            if (slot4 != null) slot4.setVisibility(View.VISIBLE);
            if (id_txtslot4 != null) {
                id_txtslot4.setVisibility(View.VISIBLE);
                id_txtslot4.setText(player4Name);
            }
        }
        // Cập nhật UI cho slot 1 và 2 dựa trên tên người chơi hiện tại
        updatePlayerSlotUI(slot1, id_txtslot1, player1Name);
        updatePlayerSlotUI(slot2, id_txtslot2, player2Name);
    }

    private void updatePlayerSlotUI(ImageButton slotButton, TextView slotTextView, String playerName) {
        // Cập nhật giao diện của ô người chơi: hiển thị TextView nếu có tên, ImageButton nếu là placeholder
        if (playerName != null && !playerName.isEmpty() &&
                !playerName.equals("Ảnh") && !playerName.equals("Đồng") &&
                !playerName.equals("Hữu") && !playerName.equals("Quyền")) { // Kiểm tra nếu đã chọn người chơi
            slotButton.setVisibility(View.GONE);
            slotTextView.setVisibility(View.VISIBLE);
            slotTextView.setText(playerName);
            slotTextView.setBackgroundColor(Color.parseColor("#2A77DB")); // Màu nền khi đã chọn
            slotTextView.setTextColor(Color.WHITE);
            slotTextView.setTextSize(18f);
            slotTextView.setGravity(Gravity.CENTER);
            slotTextView.setPadding(0, 10, 0, 10);
        } else {
            slotButton.setVisibility(View.VISIBLE);
            slotTextView.setVisibility(View.VISIBLE);
            slotTextView.setText(playerName); // Vẫn hiển thị tên placeholder
            slotTextView.setBackground(null); // Xóa nền nếu là placeholder
            slotTextView.setTextColor(Color.WHITE); // Màu chữ mặc định cho placeholder
            slotTextView.setTextSize(12f); // Kích thước chữ mặc định cho placeholder
            slotTextView.setGravity(Gravity.CENTER); // Đảm bảo placeholder cũng căn giữa
            slotTextView.setPadding(0, 0, 0, 0); // Reset padding
        }
    }

    private void updateSetScoreInputs(int numberOfSets) {
        if (layoutMatchSetScores == null) {
            Log.e("AddMatch_Fragment", "layoutMatchSetScores is null. Please ensure it's initialized.");
            return;
        }

        layoutMatchSetScores.removeAllViews(); // Xóa tất cả các ô nhập điểm hiện có

        // Thêm các ô nhập điểm mới tương ứng với số set
        for (int i = 1; i <= numberOfSets; i++) {
            View setItemView = getLayoutInflater().inflate(R.layout.item_match_set_score, layoutMatchSetScores, false);
            TextView textViewSetLabel = setItemView.findViewById(R.id.textViewSetLabel);
            textViewSetLabel.setText("Set " + i);

            layoutMatchSetScores.addView(setItemView);
        }
    }

    private void confirmMatch() {
        String matchTime = inputTimeButton.getText().toString();
        String matchDate = selectDateButton.getText().toString();
        String fullDateTime = matchDate + " " + matchTime;
        String format = currentMatchFormat.equals("single") ? "Đấu đơn" : "Đấu đôi";

        StringBuilder scoresBuilder = new StringBuilder();
        // Lặp qua từng set để lấy điểm số
        for (int i = 0; i < layoutMatchSetScores.getChildCount(); i++) {
            View setItemView = layoutMatchSetScores.getChildAt(i);
            EditText etScoreTeam1 = setItemView.findViewById(R.id.editTextScoreTeam1);
            EditText etScoreTeam2 = setItemView.findViewById(R.id.editTextScoreTeam2);

            String score1 = etScoreTeam1.getText().toString();
            String score2 = etScoreTeam2.getText().toString();

            // Kiểm tra xem điểm số đã được nhập đầy đủ chưa
            if (score1.isEmpty() || score2.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ điểm số cho tất cả các set!", Toast.LENGTH_SHORT).show();
                return; // Dừng lại nếu thiếu điểm
            }

            scoresBuilder.append(score1).append("-").append(score2);
            if (i < layoutMatchSetScores.getChildCount() - 1) {
                scoresBuilder.append(", "); // Thêm dấu phẩy nếu không phải set cuối cùng
            }
        }
        String scores = scoresBuilder.toString();

        // Chuẩn bị tên người chơi dựa trên định dạng trận đấu
        String p1 = player1Name;
        String p2 = player2Name;
        String p3 = (currentMatchFormat.equals("double") && player3Name != null && !player3Name.equals("Hữu")) ? player3Name : "";
        String p4 = (currentMatchFormat.equals("double") && player4Name != null && !player4Name.equals("Quyền")) ? player4Name : "";


        // Tạo đối tượng Matches mới
        Matches newMatch;
        if (currentMatchFormat.equals("single")) {
            newMatch = new Matches(p1, p2, R.drawable.avatar_1, R.drawable.avatar_1, scores, fullDateTime, "Đã kết thúc");
        } else {
            newMatch = new Matches(p1 + " & " + p3, p2 + " & " + p4, R.drawable.avatar_1, R.drawable.avatar_1, scores, fullDateTime, "Đã kết thúc");
        }

        // Gửi trận đấu mới
        matchDataManager.submitNewMatch(newMatch, new MatchDataManager.SubmitMatchCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                // Sau khi lưu thành công, thoát Fragment
                requireActivity().onBackPressed();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean hasUnsavedData() {
        // Kiểm tra xem người chơi đã được chọn ngoài giá trị mặc định chưa
        boolean playersSelected = !(player1Name.equals("Ảnh") && player2Name.equals("Đồng"));
        if (currentMatchFormat.equals("double")) {
            playersSelected = playersSelected && !(player3Name.equals("Hữu") && player4Name.equals("Quyền"));
        }

        // Kiểm tra xem có điểm số nào được nhập chưa
        boolean scoresEntered = false;
        if (layoutMatchSetScores != null) {
            for (int i = 0; i < layoutMatchSetScores.getChildCount(); i++) {
                View setItemView = layoutMatchSetScores.getChildAt(i);
                EditText etScoreTeam1 = setItemView.findViewById(R.id.editTextScoreTeam1);
                EditText etScoreTeam2 = setItemView.findViewById(R.id.editTextScoreTeam2);
                if (etScoreTeam1 != null && !etScoreTeam1.getText().toString().isEmpty() ||
                        etScoreTeam2 != null && !etScoreTeam2.getText().toString().isEmpty()) {
                    scoresEntered = true;
                    break;
                }
            }
        }
        return playersSelected || scoresEntered; // Trả về true nếu có bất kỳ dữ liệu nào đã thay đổi
    }

    private void showDiscardChangesDialog() {
        // Tạo một Dialog tùy chỉnh thay vì AlertDialog mặc định
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // Bỏ title mặc định của dialog
        dialog.setContentView(R.layout.dialog_discard_changes); // Gán layout tùy chỉnh của bạn

        // Để dialog có nền trong suốt (chỉ hiển thị phần bo góc của layout)
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Lấy các nút từ layout của dialog
        Button btnConfirmExit = dialog.findViewById(R.id.btn_confirm_exit);
        Button btnCancelExit = dialog.findViewById(R.id.btn_cancel_exit);

        // Thiết lập Listener cho các nút
        btnConfirmExit.setOnClickListener(v -> {
            dialog.dismiss(); // Đóng dialog
            // Khi người dùng xác nhận thoát, chúng ta chỉ cần gọi onBackPressed()
            // Callback đã được thiết lập ở onViewCreated sẽ tự động xử lý phần còn lại.
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        btnCancelExit.setOnClickListener(v -> {
            dialog.dismiss(); // Đóng dialog
        });

        dialog.show(); // Hiển thị dialog
    }
}