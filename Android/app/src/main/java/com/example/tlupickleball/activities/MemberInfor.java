package com.example.tlupickleball.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.tlupickleball.activities.base.BaseMember;
import com.example.tlupickleball.model.User;

import retrofit2.Call;

public class MemberInfor extends BaseMember {
    TextView tvName, tvEmail, tvGender, tvDob, tvSoloPoint, tvDoublePoint;
    ImageView ivAvatar;
    Button btnChangePassword, btnEditProfile;
    ImageButton btnBack;
    String uid;
    private static final int REQUEST_CODE_MEMBER_DETAIL = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_member_infor);

        tvName = findViewById(R.id.tvPlayerNameInfor);
        tvGender = findViewById(R.id.tvGenderInfor);
        tvDob = findViewById(R.id.tvDobInfor);
        tvEmail = findViewById(R.id.tvPlayerEmailWData);
        tvSoloPoint = findViewById(R.id.tvtvSoloPointWData);
        tvDoublePoint = findViewById(R.id.tvtvDuoPointWData);
        ivAvatar = findViewById(R.id.imgAvatar);

        btnChangePassword = findViewById(R.id.btn_change_password);
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnBack = findViewById(R.id.btn_back);

        Intent intent = getIntent();
        uid = getIntent().getStringExtra("uid");

        LoadUserProfile(this, uid);

        btnBack.setOnClickListener(v -> onBackPressed());

        btnChangePassword.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intentChangePass = new Intent(context, Change_Password.class);
            intentChangePass.putExtra("uid", uid);
            context.startActivity(intentChangePass);
        });
        btnEditProfile.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intentEditProfile = new Intent(context, EditMemberInfor.class);
            intentEditProfile.putExtra("uid", uid);
            startActivityForResult(intentEditProfile, REQUEST_CODE_MEMBER_DETAIL);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MEMBER_DETAIL && resultCode == RESULT_OK) {
            LoadUserProfile(this, uid); // Chỉ load lại khi có RESULT_OK
        }
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
            }
        });
    }
}