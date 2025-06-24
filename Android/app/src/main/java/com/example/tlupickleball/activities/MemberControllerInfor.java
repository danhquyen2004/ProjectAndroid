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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.tlupickleball.R;
import com.example.tlupickleball.activities.base.BaseActivity;
import com.example.tlupickleball.activities.base.BaseMember;
import com.example.tlupickleball.model.User;
import com.example.tlupickleball.network.core.ApiClient;
import com.example.tlupickleball.network.service.UserService;

import retrofit2.Call;

public class MemberControllerInfor extends BaseMember {
    TextView tvName, tvEmail, tvGender, tvDob, tvSoloPoint, tvDoublePoint;
    ImageView ivAvatar;
    Dialog dialogForm;
    Button btnDisable, btnDelete;
    ImageButton btnBack;
    String uid;
    private boolean isDialogShowing = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_member_controller_infor);
        tvName = findViewById(R.id.tvPlayerNameInfor);
        tvGender = findViewById(R.id.tvGenderInfor);
        tvDob = findViewById(R.id.tvDobInfor);
        tvEmail = findViewById(R.id.tvPlayerEmailWData);
        tvSoloPoint = findViewById(R.id.tvtvSoloPointWData);
        tvDoublePoint = findViewById(R.id.tvtvDuoPointWData);
        ivAvatar = findViewById(R.id.imgAvatar);

        btnDisable = findViewById(R.id.btn_approve);
        btnDelete = findViewById(R.id.btn_reject);
        btnBack = findViewById(R.id.btn_back);

        Intent intent = getIntent();

        // In MemberControllerInfor.java
        uid = getIntent().getStringExtra("uid");

        LoadUserProfile(this, uid);

        btnBack.setOnClickListener(v -> onBackPressed());

        btnDisable.setOnClickListener(v -> showDialogForm(true));
        btnDelete.setOnClickListener(v -> showDialogForm(false));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void showDialogForm(boolean isDisable) {
        if (isDialogShowing) {
            return; // Nếu dialog đã hiển thị, không làm gì cả
        }

        dialogForm = new Dialog(MemberControllerInfor.this);
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
        if (isDisable) {
            iconDialog.setImageResource(R.drawable.info_fill_gray);
            titleDialog.setText("Xác nhận vô hiệu hóa");
            messageDialog.setText("Bạn có chắc chắn muốn vô hiệu hóa người chơi này?");
            btnDiaLogOK.setText("Xác nhận");
        } else {
            titleDialog.setText("Xác nhận xóa tài khoản");
            messageDialog.setText("Bạn có chắc chắn muốn xóa tài khoản của người chơi này?");
            btnDiaLogOK.setText("Xác nhận");
        }

        btnDiaLogOK.setOnClickListener(v -> {
            // Xử lý logic phê duyệt/từ chối
            if (isDisable) {
                // TODO: Xử lý phê duyệt
                disableMember(uid);

            } else {
                // TODO: Xử lý từ chối
                deleteMember(uid);
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
    private void LoadUserProfile(Context context, String uid) {
        showLoading();
        userService.getUserProfileById(uid).enqueue(new retrofit2.Callback<User>() {
            @Override
            public void onResponse(Call<User> call, retrofit2.Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tvName.setText( response.body().getFullName());
                    tvGender.setText(response.body().getGender());
                    tvDob.setText(convertToVietnameseDate(response.body().getBirthDate()));
                    tvSoloPoint.setText(String.valueOf(response.body().getCurrentSingleScore()));
                    tvDoublePoint.setText(String.valueOf(response.body().getCurrentDoubleScore()));
                    tvEmail.setText(response.body().getEmail());
                    Glide.with(context)
                            .load(response.body().getAvatarUrl())
                            .placeholder(R.drawable.default_avatar)
                            .diskCacheStrategy(DiskCacheStrategy.NONE) // Bỏ qua cache trên đĩa
                            .skipMemoryCache(true) // Bỏ qua cache trong bộ nhớ
                            .circleCrop()
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
                LoadUserProfile(context, uid);
            }
        });
    }
    private void disableMember(String uid) {
        showLoading();
        userService.disableUser(uid).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                hideLoading();
                if (response.isSuccessful()) {
                    Toast.makeText(MemberControllerInfor.this, "Vô hiệu hóa thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(MemberControllerInfor.this, "Vô hiệu hóa thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                hideLoading();
                Toast.makeText(MemberControllerInfor.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteMember(String uid) {
        showLoading();
        userService.deleteUser(uid).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                hideLoading();
                if (response.isSuccessful()) {
                    Toast.makeText(MemberControllerInfor.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(MemberControllerInfor.this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                hideLoading();
                Toast.makeText(MemberControllerInfor.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}