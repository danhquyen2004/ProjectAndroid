package com.example.tlupickleball.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tlupickleball.R;

public class Change_Password extends AppCompatActivity {
    EditText edtOldPassword, edtNewPassword, edtConfirmPassword;
    Button ConfirmButton, btnBack;
    private boolean isDialogShowing = false;
    Dialog dialogForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);

        edtOldPassword = findViewById(R.id.edt_password_current);
        edtNewPassword = findViewById(R.id.edt_new_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_new_password);
        ConfirmButton = findViewById(R.id.btn_confirm);
        btnBack = findViewById(R.id.btn_back);

        ConfirmButton.setOnClickListener(v -> showDialogForm());
        btnBack.setOnClickListener(v -> onBackPressed());

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

        dialogForm = new Dialog(Change_Password.this);
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

        iconDialog.setImageResource(R.drawable.info_green_fill);
        titleDialog.setText("Xác nhận đổi mật khẩu");
        messageDialog.setText("Bạn có chắc chắn muốn đổi mật khẩu");
        btnDiaLogOK.setText("Xác nhận");

        btnDiaLogOK.setOnClickListener(v -> {
            // Xử lý logic phê duyệt/từ chối
            isDialogShowing = false; // Đánh dấu dialog đã đóng
            dialogForm.dismiss();
            finish();
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