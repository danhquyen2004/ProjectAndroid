package com.example.tlupickleball.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;

import com.example.tlupickleball.R;
import com.example.tlupickleball.activities.base.BaseActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailVerificationActivity extends BaseActivity {
    private FirebaseUser user;
    private Handler handler;
    private Runnable checkVerifiedRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        user = FirebaseAuth.getInstance().getCurrentUser();
        handler = new Handler(Looper.getMainLooper());

        Button resendButton = findViewById(R.id.resendButton);
        resendButton.setOnClickListener(v -> resendVerificationEmail());

        startEmailVerificationPolling();
    }

    private void startEmailVerificationPolling() {
        checkVerifiedRunnable = new Runnable() {
            @Override
            public void run() {
                user.reload().addOnSuccessListener(unused -> {
                    if (user.isEmailVerified()) {
                        Toast.makeText(EmailVerificationActivity.this, "Email verified!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EmailVerificationActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        handler.postDelayed(this, 5000); // kiểm tra lại sau 5 giây
                    }
                });
            }
        };

        handler.postDelayed(checkVerifiedRunnable, 5000); // chạy lần đầu sau 5s
    }

    private void resendVerificationEmail() {
        showLoading();
        user.sendEmailVerification()
                .addOnSuccessListener(aVoid -> {
                    hideLoading();
                    Toast.makeText(this, "Verification email sent again.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    hideLoading();
                    Toast.makeText(this, "Failed to resend: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(checkVerifiedRunnable);
    }
}
