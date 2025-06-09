package com.example.tlupickleball.fragments;

import android.animation.LayoutTransition;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tlupickleball.R;
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

    // Các nút thêm người chơi trên sân
    private ImageButton slot1, slot2, slot3, slot4;
    // Các TextView hiển thị tên người chơi
    private TextView id_txtslot1, id_txtslot2, id_txtslot3, id_txtslot4;

    private Calendar selectedDateTime;

    // --- Biến cho Tab Layout (Đấu đơn/Đấu đôi) ---
    private TextView item1, item2, select; // item1 là "Đấu đơn", item2 là "Đấu đôi", select là thanh trượt
    private FrameLayout tabContentFrame; // FrameLayout chứa các tab và thanh trượt
    private String currentMatchFormat = "double"; // Mặc định là "đấu đôi" để hiển thị 4 slot ban đầu

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_match, container, false);

        // Ánh xạ các View hiện có
        backButton = root.findViewById(R.id.backButton);
        inputTimeButton = root.findViewById(R.id.inputTimeButton);
        selectDateButton = root.findViewById(R.id.selectDateButton);
        setCountSpinner = root.findViewById(R.id.setCountSpinner);
        confirmButton = root.findViewById(R.id.confirmButton);

        // Ánh xạ các nút và TextView cho slot người chơi (từ view_pickleball_court.xml)
        slot1 = root.findViewById(R.id.slot1);
        slot2 = root.findViewById(R.id.slot2);
        slot3 = root.findViewById(R.id.slot3);
        slot4 = root.findViewById(R.id.slot4);

        id_txtslot1 = root.findViewById(R.id.id_txtslot1);
        id_txtslot2 = root.findViewById(R.id.id_txtslot2);
        id_txtslot3 = root.findViewById(R.id.id_txtslot3);
        id_txtslot4 = root.findViewById(R.id.id_txtslot4);

        // --- Ánh xạ các View từ view_tablayout (được include trong fragment_add_match.xml) ---
        item1 = root.findViewById(R.id.item1); // TextView "Đấu đơn"
        item2 = root.findViewById(R.id.item2); // TextView "Đấu đôi"
        select = root.findViewById(R.id.select); // Thanh trượt màu xanh
        tabContentFrame = root.findViewById(R.id.tab_content_frame); // FrameLayout chứa 2 TextView và thanh trượt

        selectedDateTime = Calendar.getInstance();

        // Thiết lập Spinner cho số set đấu
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.number_of_sets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setCountSpinner.setAdapter(adapter);

        // Thiết lập Listener cho các nút
        backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        inputTimeButton.setOnClickListener(v -> showTimePickerDialog());
        selectDateButton.setOnClickListener(v -> showDatePickerDialog());

        // Thiết lập Listener cho các slot người chơi
        setupSlotClickListeners();

        confirmButton.setOnClickListener(v -> {
            // Logic xử lý khi nhấn Xác nhận
            // Bạn cần lấy dữ liệu từ các TextView người chơi, thời gian, ngày, số set...
            // Sau đó tạo đối tượng Matches và lưu trữ/gửi đi
            Toast.makeText(getContext(), "Xác nhận ghi nhận trận đấu", Toast.LENGTH_SHORT).show();
            // Ví dụ: Ghi log tên người chơi hiện tại
            Log.d("AddMatchFragment", "Player 1: " + id_txtslot1.getText().toString());
            Log.d("AddMatchFragment", "Player 2: " + id_txtslot2.getText().toString());
            if (currentMatchFormat.equals("double")) {
                Log.d("AddMatchFragment", "Player 3: " + id_txtslot3.getText().toString());
                Log.d("AddMatchFragment", "Player 4: " + id_txtslot4.getText().toString());
            }
            // Quay lại fragment trước đó (Matches_Fragment)
            getParentFragmentManager().popBackStack();
        });

        // --- Thiết lập Listener và trạng thái ban đầu cho các tab Đơn/Đôi ---
        setupMatchFormatTabs();

        return root;
    }

    private void setupSlotClickListeners() {
        // Thiết lập listener cho slot 1 và 2 (luôn hiển thị)
        slot1.setOnClickListener(v -> openAddPlayerDialog(1));
        slot2.setOnClickListener(v -> openAddPlayerDialog(2));

        // Thiết lập listener cho slot 3 và 4 (chỉ cho phép click khi ở chế độ đấu đôi)
        slot3.setOnClickListener(v -> {
            if (currentMatchFormat.equals("double")) {
                openAddPlayerDialog(3);
            } else {
                Toast.makeText(getContext(), "Vị trí này chỉ khả dụng cho đấu đôi.", Toast.LENGTH_SHORT).show();
            }
        });
        slot4.setOnClickListener(v -> {
            if (currentMatchFormat.equals("double")) {
                openAddPlayerDialog(4);
            } else {
                Toast.makeText(getContext(), "Vị trí này chỉ khả dụng cho đấu đôi.", Toast.LENGTH_SHORT).show();
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
                    updateTimeButtonText();
                }, hour, minute, true); // true cho định dạng 24h
        timePickerDialog.show();
    }

    private void updateTimeButtonText() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        inputTimeButton.setText(sdf.format(selectedDateTime.getTime()));
    }

    private void showDatePickerDialog() {
        int year = selectedDateTime.get(Calendar.YEAR);
        int month = selectedDateTime.get(Calendar.MONTH);
        int day = selectedDateTime.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year1, month1, dayOfMonth) -> {
                    selectedDateTime.set(year1, month1, dayOfMonth);
                    updateDateButtonText();
                }, year, month, day);
        datePickerDialog.show();
    }

    private void updateDateButtonText() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        selectDateButton.setText(sdf.format(selectedDateTime.getTime()));
    }

    // Xử lý logic hiển thị pop-up thêm người chơi
    private void openAddPlayerDialog(int slotNumber) {
        // Đây là nơi bạn sẽ hiển thị AlertDialog hoặc BottomSheetDialog
        // với layout dialog_add_player.xml đã tạo trước đó.
        // Trong dialog đó, bạn sẽ có EditText để tìm kiếm và RecyclerView để hiển thị danh sách người chơi.

        // Ví dụ đơn giản hiển thị Toast và gán tên tạm thời
        Toast.makeText(getContext(), "Mở pop-up để thêm người chơi cho vị trí " + slotNumber, Toast.LENGTH_SHORT).show();

        // Sau khi người dùng chọn người chơi từ dialog, bạn sẽ cập nhật TextView tương ứng:
        // Ví dụ:
        String selectedPlayerName = "Người chơi " + slotNumber; // Giả sử tên được chọn
        switch (slotNumber) {
            case 1:
                id_txtslot1.setText(selectedPlayerName);
                break;
            case 2:
                id_txtslot2.setText(selectedPlayerName);
                break;
            case 3:
                id_txtslot3.setText(selectedPlayerName);
                break;
            case 4:
                id_txtslot4.setText(selectedPlayerName);
                break;
        }
        // Để triển khai pop-up đầy đủ, bạn sẽ cần tạo một DialogFragment riêng
        // hoặc xây dựng AlertDialog trực tiếp ở đây với Custom View.
    }

    // --- Các phương thức cho việc xử lý tab Đơn/Đôi ---

    /**
     * Thiết lập các listener cho các tab "Đấu đơn" và "Đấu đôi" và quản lý hiệu ứng chuyển động.
     */
    private void setupMatchFormatTabs() {
        // Tìm parent LinearLayout của item1.
        // Dựa trên view_tablayout.xml, item1 nằm trực tiếp trong một LinearLayout.
        LinearLayout parentLinearLayout = null;
        if (item1 != null && item1.getParent() instanceof LinearLayout) {
            parentLinearLayout = (LinearLayout) item1.getParent();
        }

        // Kích hoạt LayoutTransition để có hiệu ứng chuyển động mượt mà cho thanh trượt
        if (parentLinearLayout != null) {
            // Kiểm tra nếu LayoutTransition là null, nếu có, hãy tạo một LayoutTransition mới
            if (parentLinearLayout.getLayoutTransition() == null) {
                parentLinearLayout.setLayoutTransition(new LayoutTransition());
            }
            // Bây giờ bạn có thể an toàn gọi enableTransitionType
            parentLinearLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        } else {
            Log.e("AddMatchFragment", "Parent LinearLayout for tabs not found or not a LinearLayout.");
        }


        // Listener cho tab "Đấu đơn"
        if (item1 != null) {
            item1.setOnClickListener(v -> {
                if (!currentMatchFormat.equals("single")) { // Chỉ cập nhật nếu khác trạng thái hiện tại
                    currentMatchFormat = "single";
                    updateTabFormatSelection(currentMatchFormat); // Cập nhật UI của tab
                    updatePlayerSlotsVisibility(); // Cập nhật hiển thị các slot người chơi
                }
            });
        }

        // Listener cho tab "Đấu đôi"
        if (item2 != null) {
            item2.setOnClickListener(v -> {
                if (!currentMatchFormat.equals("double")) { // Chỉ cập nhật nếu khác trạng thái hiện tại
                    currentMatchFormat = "double";
                    updateTabFormatSelection(currentMatchFormat); // Cập nhật UI của tab
                    updatePlayerSlotsVisibility(); // Cập nhật hiển thị các slot người chơi
                }
            });
        }

        // Thiết lập trạng thái ban đầu của tab và các slot khi Fragment được tạo
        updateTabFormatSelection(currentMatchFormat);
        updatePlayerSlotsVisibility();
    }

    /**
     * Cập nhật UI của tab layout (vị trí thanh chọn và màu chữ) dựa trên loại trận đấu được chọn.
     *
     * @param selectedFormat "single" cho Đấu đơn, "double" cho Đấu đôi.
     */
    private void updateTabFormatSelection(String selectedFormat) {
        if (tabContentFrame == null || select == null || item1 == null || item2 == null) {
            Log.e("AddMatchFragment", "Tab layout views not initialized.");
            return;
        }

        // Đảm bảo layout đã được đo để tránh lỗi getWidth() = 0
        if (tabContentFrame.getWidth() == 0) {
            tabContentFrame.post(() -> updateTabFormatSelection(selectedFormat));
            return;
        }

        int tabWidth = tabContentFrame.getWidth() / 2; // Chiều rộng của mỗi tab
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) select.getLayoutParams();
        params.width = tabWidth; // Đặt chiều rộng của thanh trượt

        if (selectedFormat.equals("single")) {
            params.setMarginStart(0); // Đặt thanh trượt ở vị trí tab đầu tiên
            select.setLayoutParams(params);
            item1.setTextColor(Color.WHITE); // Màu chữ trắng cho tab đang chọn
            item2.setTextColor(Color.BLACK); // Màu chữ đen cho tab không chọn
        } else if (selectedFormat.equals("double")) {
            params.setMarginStart(tabWidth); // Đặt thanh trượt ở vị trí tab thứ hai
            select.setLayoutParams(params);
            item1.setTextColor(Color.BLACK);
            item2.setTextColor(Color.WHITE);
        }
    }

    /**
     * Cập nhật visibility của các slot người chơi dựa trên loại trận đấu được chọn.
     * - "single": chỉ hiển thị slot 1 và 2.
     * - "double": hiển thị tất cả slot 1, 2, 3, 4.
     */
    private void updatePlayerSlotsVisibility() {
        if (currentMatchFormat.equals("single")) {
            // Ẩn slot 3 và 4 cùng với TextView của chúng
            if (slot3 != null) slot3.setVisibility(View.GONE);
            if (id_txtslot3 != null) {
                id_txtslot3.setVisibility(View.GONE);
                id_txtslot3.setText(""); // Xóa văn bản nếu đang ẩn
            }
            if (slot4 != null) slot4.setVisibility(View.GONE);
            if (id_txtslot4 != null) {
                id_txtslot4.setVisibility(View.GONE);
                id_txtslot4.setText(""); // Xóa văn bản nếu đang ẩn
            }
        } else { // "double"
            // Hiển thị tất cả các slot và TextView của chúng
            if (slot3 != null) slot3.setVisibility(View.VISIBLE);
            if (id_txtslot3 != null) id_txtslot3.setVisibility(View.VISIBLE);
            if (slot4 != null) slot4.setVisibility(View.VISIBLE);
            if (id_txtslot4 != null) id_txtslot4.setVisibility(View.VISIBLE);
        }
    }
}