package com.example.tlupickleball.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.tlupickleball.R;
import com.example.tlupickleball.activities.base.BaseActivity;
import com.example.tlupickleball.activities.base.BaseMember;
import com.example.tlupickleball.model.User;

import retrofit2.Call;

public class ApproveMemberInfor extends BaseMember {

    TextView tvName, tvEmail, tvGender, tvDob;
    ImageView ivAvatar;
    Dialog dialogForm;
    Button btnApprove, btnReject;
    ImageButton btnBack;
    String uid;
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
        uid = getIntent().getStringExtra("uid");

        showLoading();
        LoadUserProfile(this, uid);

        btnBack.setOnClickListener(v -> onBackPressed());

        btnApprove.setOnClickListener(v -> showDialogForm(true));
        btnReject.setOnClickListener(v -> showDialogForm(false));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void LoadUserProfile(Context context, String uid) {
        userService.getUserProfileById(uid).enqueue(new retrofit2.Callback<User>() {
            @Override
            public void onResponse(Call<User> call, retrofit2.Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tvName.setText( response.body().getFullName());
                    tvGender.setText(response.body().getGender());
                    tvDob.setText(convertToVietnameseDate(response.body().getBirthDate()));
                    tvEmail.setText(response.body().getEmail());
                    Glide.with(context)
                            .load(response.body().getAvatarUrl())
                            .placeholder(R.drawable.avatar_1)
                            .into(ivAvatar);
                    hideLoading();
                } else {
                    Toast.makeText(context, "Không có dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                    hideLoading();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(context, "Không thể truy vấn hồ sơ người dùng", Toast.LENGTH_SHORT).show();
                hideLoading();
            }
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
                approveMember(uid);
            } else {
                // TODO: Xử lý từ chối
                rejectMember(uid);
            }
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
    private void approveMember(String uid) {
        showLoading();
        userService.approveUser(uid).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                hideLoading();
                if (response.isSuccessful()) {
                    Toast.makeText(ApproveMemberInfor.this, "Phê duyệt thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(ApproveMemberInfor.this, "Phê duyệt thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                hideLoading();
                Toast.makeText(ApproveMemberInfor.this, "Phê kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void rejectMember(String uid) {
        showLoading();
        userService.rejectUser(uid).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                hideLoading();
                if (response.isSuccessful()) {
                    Toast.makeText(ApproveMemberInfor.this, "Từ chối thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(ApproveMemberInfor.this, "Từ chối thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                hideLoading();
                Toast.makeText(ApproveMemberInfor.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}