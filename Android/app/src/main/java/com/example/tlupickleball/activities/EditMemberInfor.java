package com.example.tlupickleball.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tlupickleball.R;

import android.app.DatePickerDialog;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import java.util.Calendar;

public class EditMemberInfor extends AppCompatActivity {
    EditText edtName, edtGender, edtDob;
    ImageView ivAvatar;
    Button btn_Accept;
    ImageButton btnBack;
    boolean isDialogShowing;
    Dialog dialogForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_member_infor);

        edtName = findViewById(R.id.edtPlayerNameInfor);
        edtGender = findViewById(R.id.edtGenderInfor);
        edtDob = findViewById(R.id.edtDobInfor);
        ivAvatar = findViewById(R.id.imgAvatar);

        btn_Accept = findViewById(R.id.btn_accept);
        btnBack = findViewById(R.id.btn_back);


//        edtName.setOnFocusChangeListener((v, hasFocus) -> {
//            if (hasFocus) {
//                edtName.setTextColor(Color.BLACK);
//                edtName.setTypeface(null, Typeface.BOLD);
//            } else {
//                edtName.setTextColor(ContextCompat.getColor(this, R.color.));
//                edtName.setTypeface(null, Typeface.NORMAL);
//            }
//        });
//
//        edtDob.setOnFocusChangeListener((v, hasFocus) -> {
//            if (hasFocus) {
//                edtDob.setTextColor(Color.BLACK);
//                edtDob.setTypeface(null, Typeface.BOLD);
//            } else {
//                edtDob.setTextColor(ContextCompat.getColor(this, R.color.your_default_color));
//                edtDob.setTypeface(null, Typeface.NORMAL);
//            }
//        });

        // Gender dropdown
        AutoCompleteTextView genderView = findViewById(R.id.edtGenderInfor);
        String[] genderOptions = {"Nam", "Nữ"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, genderOptions);
        genderView.setAdapter(adapter);
        genderView.setFocusable(false);
        genderView.setOnClickListener(v -> genderView.showDropDown());

        EditText dobEditText = findViewById(R.id.edtDobInfor);

        dobEditText.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                if (!input.equals(current)) {
                    String clean = input.replaceAll("[^\\d]", "");
                    String cleanC = current.replaceAll("[^\\d]", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        clean = clean + ddmmyyyy.substring(clean.length());
                    }

                    String formatted = clean.substring(0,2) + "/" + clean.substring(2,4) + "/" + clean.substring(4,8);

                    current = formatted;
                    dobEditText.setText(formatted);
                    dobEditText.setSelection(sel < current.length() ? sel : current.length());

                    // Only validate if all are digits and length is 8
                    if (clean.length() == 8 && clean.matches("\\d{8}")) {
                        int day = Integer.parseInt(clean.substring(0,2));
                        int month = Integer.parseInt(clean.substring(2,4));
                        int year = Integer.parseInt(clean.substring(4,8));

                        Calendar now = Calendar.getInstance();
                        int currentYear = now.get(Calendar.YEAR);

                        boolean valid = true;
                        String errorMsg = null;

                        if (day < 1 || month < 1 || month > 12 || year < 1900 || year > currentYear) {
                            valid = false;
                            errorMsg = "Ngày, tháng hoặc năm không hợp lệ";
                        } else {
                            Calendar cal = Calendar.getInstance();
                            cal.setLenient(false);
                            cal.set(year, month - 1, day);
                            try {
                                cal.getTime();
                                Calendar inputDate = Calendar.getInstance();
                                inputDate.set(year, month - 1, day, 0, 0, 0);
                                inputDate.set(Calendar.MILLISECOND, 0);
                                if (inputDate.after(now)) {
                                    valid = false;
                                    errorMsg = "Không được nhập ngày trong tương lai";
                                }
                            } catch (Exception e) {
                                valid = false;
                                errorMsg = "Ngày không hợp lệ";
                            }
                        }
                        if (!valid) {
                            dobEditText.setError(errorMsg);
                        } else {
                            dobEditText.setError(null);
                        }
                    } else if (input.length() > 0 && input.length() < 10) {
                        dobEditText.setError("Định dạng: dd/MM/yyyy");
                    } else {
                        dobEditText.setError(null);
                    }
                }
            }
        });

//        Intent intent = getIntent();
//        tvName.setText(intent.getStringExtra("name"));
//        tvEmail.setText(intent.getStringExtra("email"));
//        ivAvatar.setImageResource(intent.getIntExtra("avatar", 0));

        btnBack.setOnClickListener(v -> onBackPressed());

        btn_Accept.setOnClickListener(v -> {
           showDialogForm();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void showDialogForm() {
        if (isDialogShowing) {
            return; // Nếu dialog đã hiển thị, không làm gì cả
        }

        dialogForm = new Dialog(EditMemberInfor.this);
        dialogForm.setContentView(R.layout.view_dialog_form);
        dialogForm.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogForm.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_box));
        dialogForm.setCancelable(false);

        isDialogShowing = true;

        // Tìm các view trong dialog
        ImageView iconDialog = dialogForm.findViewById(R.id.icon_dialog);
        TextView titleDialog = dialogForm.findViewById(R.id.tv_confirm);
        TextView messageDialog = dialogForm.findViewById(R.id.tv_confirm_infor);
        Button btnDiaLogOK = dialogForm.findViewById(R.id.btn_approve);
        Button btnDiaLogCancel = dialogForm.findViewById(R.id.btn_reject);


        iconDialog.setImageResource(R.drawable.info_warning_fill);
        titleDialog.setText("Bạn chắc chắn muốn thay đổi thông tin");
        messageDialog.setText("Thông tin sẽ được cập nhật ngay lập tức, bạn có chắc chắn muốn tiếp tục?");
        btnDiaLogOK.setText("Xác nhận");
        btnDiaLogCancel.setText("Quay lại");

        btnDiaLogOK.setOnClickListener(v -> {
            // Xử lý logic
            isDialogShowing = false; // Đánh dấu dialog đã đóng
            dialogForm.dismiss();
        });

        btnDiaLogCancel.setOnClickListener(v -> {
            isDialogShowing = false; // Reset trạng thái
            dialogForm.dismiss();
        });

        // Thêm listener khi dialog bị dismiss
        dialogForm.setOnDismissListener(dialog -> {
            isDialogShowing = false;
        });

        dialogForm.show();
    }
}