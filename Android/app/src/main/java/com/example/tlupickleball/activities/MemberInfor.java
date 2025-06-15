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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tlupickleball.R;

public class MemberInfor extends AppCompatActivity {
    TextView tvName, tvEmail, tvGender, tvDob, tvSoloPoint, tvDoublePoint;
    ImageView ivAvatar;
    Button btnChangePassword, btnEditProfile;
    ImageButton btnBack;

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

//        Intent intent = getIntent();
//        tvName.setText(intent.getStringExtra("name"));
//        tvEmail.setText(intent.getStringExtra("email"));
//        ivAvatar.setImageResource(intent.getIntExtra("avatar", 0));

        btnBack.setOnClickListener(v -> onBackPressed());

        btnChangePassword.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intentChangePass = new Intent(context, MemberControllerInfor.class);
//            intent.putExtra("name", player.getName());
//            intent.putExtra("email", player.getEmail());
//            intent.putExtra("avatar", player.getAvatarResourceId());
            context.startActivity(intentChangePass);
        });
        btnEditProfile.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intentEditProfile = new Intent(context, MemberControllerInfor.class);
//            intent.putExtra("name", player.getName());
//            intent.putExtra("email", player.getEmail());
//            intent.putExtra("avatar", player.getAvatarResourceId());
            context.startActivity(intentEditProfile);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}