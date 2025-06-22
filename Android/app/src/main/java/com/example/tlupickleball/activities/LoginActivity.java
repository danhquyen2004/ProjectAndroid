package com.example.tlupickleball.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



import com.example.tlupickleball.R;
import com.example.tlupickleball.activities.base.AuthActivity;
import com.example.tlupickleball.model.Account;
import com.example.tlupickleball.model.User;
import com.example.tlupickleball.network.api_model.auth.LoginResponse;
import com.example.tlupickleball.network.service.UserService;
import com.example.tlupickleball.network.core.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AuthActivity {
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView goToRegisterText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        goToRegisterText = findViewById(R.id.goToRegisterText);

        loginButton.setOnClickListener(v -> {
            showLoading();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            loginUser(email, password);
        });

        goToRegisterText.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }
    private void loginUser(String email, String password) {
        Account account = new Account(email, password);
        authService.login(account).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse body = response.body();
                    SessionManager.saveTokens(LoginActivity.this, body.getIdToken(), body.getRefreshToken(), body.getUid());
                    if(body.isDisabled())
                    {
                        Toast.makeText(LoginActivity.this, "Tài khoản đã bị vô hiệu hóa", Toast.LENGTH_SHORT).show();
                    }
                    else if(!body.isEmailVerified())
                    {
                        startActivity(new Intent(LoginActivity.this, EmailVerificationActivity.class));
                    }
                    else
                    {
                        Log.d("TOKEN", "TOKEN : " + body.getIdToken());
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        checkUserProfileAndRedirect(LoginActivity.this, body.getUid(), userService);
                    }

                } else {
                    if(response.code() == 401){
                        Toast.makeText(LoginActivity.this, "Email hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                    }
                }
                hideLoading();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("LoginActivity", "onFailure: " + t.getMessage());
                hideLoading();
            }
        });
    }

    private static void checkUserProfileAndRedirect(Context context, String uid, UserService userService) {
        userService.getUserProfileById(uid).enqueue(new retrofit2.Callback<User>() {
            @Override
            public void onResponse(Call<User> call, retrofit2.Response<User> response) {
                if (!response.isSuccessful() || response.body() == null ||
                        response.body().getFullName() == null ||
                        response.body().getBirthDate() == null ||
                        response.body().getGender() == null) {

                    context.startActivity(new Intent(context, ProfileActivity.class));
                } else {
                    context.startActivity(new Intent(context, UserActivity.class));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(context, "Không thể truy vấn hồ sơ người dùng", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
