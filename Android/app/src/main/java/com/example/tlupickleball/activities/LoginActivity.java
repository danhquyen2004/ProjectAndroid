package com.example.tlupickleball.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tlupickleball.R;
import com.example.tlupickleball.activities.base.ApiCallback;
import com.example.tlupickleball.activities.base.BaseActivity;
import com.example.tlupickleball.model.Account;
import com.example.tlupickleball.network.api_model.auth.LoginResponse;
import com.example.tlupickleball.network.auth.AuthApi;
import com.example.tlupickleball.network.core.ApiClient;
import com.example.tlupickleball.network.service.AuthService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.tlupickleball.network.core.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView goToRegisterText;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        goToRegisterText = findViewById(R.id.goToRegisterText);

        authService = ApiClient.getClient(this).create(AuthService.class);

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

                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                    checkUserProfileAndRedirect(LoginActivity.this, body.getUid());
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed: " + response.message(), Toast.LENGTH_SHORT).show();
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

    private static void checkUserProfileAndRedirect(Context context, String uid) {
        FirebaseFirestore.getInstance().collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(document -> {
                    if (!document.exists() ||
                            !document.contains("name") ||
                            !document.contains("dob") ||
                            !document.contains("gender")) {

                        context.startActivity(new Intent(context, ProfileActivity.class));
                    } else {
                        context.startActivity(new Intent(context, MainActivity.class));
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Không thể truy vấn hồ sơ người dùng", Toast.LENGTH_SHORT).show();
                });
    }

}
