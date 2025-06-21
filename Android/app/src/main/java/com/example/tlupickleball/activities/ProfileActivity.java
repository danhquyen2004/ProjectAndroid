package com.example.tlupickleball.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tlupickleball.R;
import com.example.tlupickleball.activities.base.BaseActivity;
import com.example.tlupickleball.model.User;
import com.example.tlupickleball.network.core.ApiClient;
import com.example.tlupickleball.network.service.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends BaseActivity {
    private EditText nameEditText, dobEditText, genderEditText;
    private Button submitButton;
    UserService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameEditText = findViewById(R.id.nameEditText);
        dobEditText = findViewById(R.id.dobEditText);
        genderEditText = findViewById(R.id.genderEditText);
        submitButton = findViewById(R.id.submitButton);

        service = ApiClient.getClient(this).create(UserService.class);

        submitButton.setOnClickListener(v -> {
            showLoading();

            String name = nameEditText.getText().toString().trim();
            String dob = dobEditText.getText().toString().trim();
            String gender = genderEditText.getText().toString().trim();

            saveUserProfile(name, dob,gender);
        });
    }

    private void saveUserProfile(String name, String dob, String gender)
    {
        User user = new User();
        user.setName(name);
        user.setDob(dob);
        user.setGender(gender);

        service.submitProfile(user).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Cập nhật hồ sơ thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(ProfileActivity.this, "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
                hideLoading();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                hideLoading();
            }
        });
    }
}
