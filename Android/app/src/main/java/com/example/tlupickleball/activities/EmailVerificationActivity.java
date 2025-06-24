package com.example.tlupickleball.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;

import com.example.tlupickleball.R;
import com.example.tlupickleball.activities.base.AuthActivity;
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

public class EmailVerificationActivity extends AuthActivity {
    private Handler handler;
    private Runnable checkVerifiedRunnable;

    Button resendButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);
        handler = new Handler(Looper.getMainLooper());

        resendButton = findViewById(R.id.resendButton);
        resendButton.setOnClickListener(v -> {
            resendVerificationEmail(this,
                    () -> startCooldown(this, resendButton),
                    () -> {}
            );
        });
        // Save the current time as the last sent time if this is the first entry
        if (getCooldownTimeLeft(this) == 0) {
            saveLastSentTime(this);
        }
        startCooldown(this, resendButton);
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
                                startActivity(new Intent(EmailVerificationActivity.this, ProfileActivity.class));
                                finish();
                            } else {
                                handler.postDelayed( checkVerifiedRunnable, 5000);
                            }
                        } else {
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(checkVerifiedRunnable);
    }
}
