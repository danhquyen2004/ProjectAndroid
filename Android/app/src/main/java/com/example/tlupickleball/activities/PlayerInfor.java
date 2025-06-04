package com.example.tlupickleball.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tlupickleball.R;

public class PlayerInfor extends AppCompatActivity {

    Button btnApprove, btnReject;
    ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_player_infor);

        btnApprove = findViewById(R.id.btn_approve);
        btnReject = findViewById(R.id.btn_reject);
        btnBack = findViewById(R.id.btn_back);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
        int avatarResId = intent.getIntExtra("avatar", 0);

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
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.view_dialog_form);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Tìm các view trong dialog
        TextView titleDialog = dialog.findViewById(R.id.tv_confirm);
        TextView messageDialog = dialog.findViewById(R.id.tv_confirm_infor);
        Button btnDiaLogOK = dialog.findViewById(R.id.btn_approve);
        Button btnDiaLogCancel = dialog.findViewById(R.id.btn_reject);

        // Cập nhật nội dung dialog theo trường hợp
        if (isApprove) {
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
            dialog.dismiss();
            finish();
        });

        btnDiaLogCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}