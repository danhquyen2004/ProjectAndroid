package com.example.tlupickleball.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tlupickleball.R;

public class ApproveMemberInfor extends AppCompatActivity {

    TextView tvName, tvEmail, tvGender, tvDob;
    ImageView ivAvatar;
    Dialog dialogForm;
    Button btnApprove, btnReject;
    ImageButton btnBack;
    private boolean isDialogShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_approve_member_infor);

        tvName = findViewById(R.id.tvPlayerNameInfor);
        tvGender = findViewById(R.id.tvGenderInfor);
        tvDob = findViewById(R.id.tvDobInfor);
        tvEmail = findViewById(R.id.tvPlayerEmailWData);
        ivAvatar = findViewById(R.id.imgAvatar);

        btnApprove = findViewById(R.id.btn_approve);
        btnReject = findViewById(R.id.btn_reject);
        btnBack = findViewById(R.id.btn_back);

        Intent intent = getIntent();
        tvName.setText(intent.getStringExtra("name"));
        tvEmail.setText(intent.getStringExtra("email"));
        ivAvatar.setImageResource(intent.getIntExtra("avatar", 0));

        btnBack.setOnClickListener(v -> onBackPressed());

        btnApprove.setOnClickListener(v -> showDialogForm(true));
        btnReject.setOnClickListener(v -> showDialogForm(false));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void showDialogForm(boolean isApprove) {
        if (isDialogShowing) {
            return; // Nếu dialog đã hiển thị, không làm gì cả
        }

        dialogForm = new Dialog(ApproveMemberInfor.this);
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

        // Cập nhật nội dung dialog theo trường hợp
        if (isApprove) {
            iconDialog.setImageResource(R.drawable.info_green_fill);
            titleDialog.setText("Xác nhận phê duyệt");
            messageDialog.setText("Bạn có chắc chắn muốn phê duyệt người chơi này?");
            btnDiaLogOK.setText("Phê duyệt");
        } else {
            titleDialog.setText("Xác nhận từ chối");
            messageDialog.setText("Bạn có chắc chắn muốn từ chối người chơi này?");
            btnDiaLogOK.setText("Từ chối");
        }

        btnDiaLogOK.setOnClickListener(v -> {
            // Xử lý logic phê duyệt/từ chối
            if (isApprove) {
                // TODO: Xử lý phê duyệt
            } else {
                // TODO: Xử lý từ chối
            }
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