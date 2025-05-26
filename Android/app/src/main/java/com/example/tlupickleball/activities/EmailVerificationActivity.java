package com.example.tlupickleball.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;

import com.example.tlupickleball.R;
import com.example.tlupickleball.activities.base.BaseActivity;
import com.example.tlupickleball.network.api_model.auth.EmailVerificationRequest;
import com.example.tlupickleball.network.api_model.auth.EmailVerificationResponse;
import com.example.tlupickleball.network.api_model.auth.GenericResponse;
import com.example.tlupickleball.network.core.ApiClient;
import com.example.tlupickleball.network.core.SessionManager;
import com.example.tlupickleball.network.service.AuthService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmailVerificationActivity extends BaseActivity {
    private Handler handler;
    private Runnable checkVerifiedRunnable;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);
        authService = ApiClient.getClient(this).create(AuthService.class);

        handler = new Handler(Looper.getMainLooper());

        Button resendButton = findViewById(R.id.resendButton);
        resendButton.setOnClickListener(v -> resendVerificationEmail());

        startEmailVerificationPolling();
    }

    private void startEmailVerificationPolling() {
        handler = new Handler(Looper.getMainLooper());

        checkVerifiedRunnable = new Runnable() {
            @Override
            public void run() {
                //String uid = SessionManager.getUid(EmailVerificationActivity.this);
                String uid = getIntent().getStringExtra("uid");
                authService.checkEmailVerified(uid).enqueue(new Callback<EmailVerificationResponse>() {
                    @Override
                    public void onResponse(Call<EmailVerificationResponse> call, Response<EmailVerificationResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            if (response.body().isEmailVerified()) {
                                Toast.makeText(EmailVerificationActivity.this, "✅ Email verified!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(EmailVerificationActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                Toast.makeText(EmailVerificationActivity.this, "✅ Email checking 1", Toast.LENGTH_SHORT).show();
                                handler.postDelayed( checkVerifiedRunnable, 5000);
                            }
                        } else {
                            Toast.makeText(EmailVerificationActivity.this, "✅ Email checking 2", Toast.LENGTH_SHORT).show();
                            handler.postDelayed(checkVerifiedRunnable, 5000);
                        }
                    }

                    @Override
                    public void onFailure(Call<EmailVerificationResponse> call, Throwable t) {
                        handler.postDelayed(checkVerifiedRunnable, 5000);
                    }
                });
            }
        };

        handler.postDelayed(checkVerifiedRunnable, 5000);
    }

    private void resendVerificationEmail() {
        showLoading();
        EmailVerificationRequest request = new EmailVerificationRequest(SessionManager.getIdToken(this));
        authService.sendVerificationEmail(request).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if(response.isSuccessful())
                {
                    hideLoading();
                    Toast.makeText(EmailVerificationActivity.this, "Verification email sent again.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    hideLoading();
                    Toast.makeText(EmailVerificationActivity.this, "Failed to resend", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {

            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(checkVerifiedRunnable);
    }
}
