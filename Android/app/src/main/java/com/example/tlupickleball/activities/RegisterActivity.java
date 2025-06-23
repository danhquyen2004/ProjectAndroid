package com.example.tlupickleball.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tlupickleball.R;
import com.example.tlupickleball.activities.base.AuthActivity;
import com.example.tlupickleball.activities.base.BaseActivity;
import com.example.tlupickleball.model.Account;
import com.example.tlupickleball.network.api_model.auth.RegisterResponse;
import com.example.tlupickleball.network.core.ApiClient;
import com.example.tlupickleball.network.core.SessionManager;
import com.example.tlupickleball.network.service.AuthService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AuthActivity {
    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton;
    private TextView goToLoginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        registerButton = findViewById(R.id.registerButton);
        goToLoginText = findViewById(R.id.goToLoginText);

        registerButton.setOnClickListener(v -> {
            showLoading();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            registerUser(email, password);

        });
        goToLoginText.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });
    }
    private void registerUser(String email, String password) {
        Account account = new Account(email, password);

        authService.register(account).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if(response.isSuccessful())
                {
                    hideLoading();
                    SessionManager.saveTokens(RegisterActivity.this, response.body().getIdToken(), response.body().getRefreshToken(), response.body().getUid());
                    Intent intent = new Intent(RegisterActivity.this, EmailVerificationActivity.class);
                    intent.putExtra("uid", response.body().getUid());
                    RegisterActivity.this.startActivity(intent);
                }
                else {
                    Toast.makeText(RegisterActivity.this, "Registration failed: " + response.message(), Toast.LENGTH_SHORT).show();
                    hideLoading();
                }

            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Registration failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                hideLoading();
            }
        });
    }
}

