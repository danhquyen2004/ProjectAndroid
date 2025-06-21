package com.example.tlupickleball.activities.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.Toast;

import com.example.tlupickleball.activities.EmailVerificationActivity;
import com.example.tlupickleball.network.api_model.auth.EmailVerificationRequest;
import com.example.tlupickleball.network.api_model.auth.GenericResponse;
import com.example.tlupickleball.network.core.ApiClient;
import com.example.tlupickleball.network.core.SessionManager;
import com.example.tlupickleball.network.service.AuthService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends BaseActivity{
    protected AuthService authService;
    protected static final long COOLDOWN_MS = 60 * 1000;
    private static final String PREF_NAME = "cooldown_prefs";
    private static final String PREF_KEY_LAST_SENT = "last_verification_email_sent";

    protected CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authService = ApiClient.getClient(this).create(AuthService.class);
    }
    protected void resendVerificationEmail(Context context, Runnable onSuccess , Runnable onFail) {
        showLoading();
        String idToken = SessionManager.getIdToken(this);
        EmailVerificationRequest request = new EmailVerificationRequest(idToken);

        authService.sendVerificationEmail(request).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                hideLoading();
                if (response.isSuccessful()) {
                    saveLastSentTime(context);
                    Toast.makeText(context, "Verification email sent.", Toast.LENGTH_SHORT).show();
                    if (onSuccess != null) onSuccess.run();
                } else {
                    Toast.makeText(context, "Failed to resend", Toast.LENGTH_LONG).show();
                    if (onFail != null) onFail.run();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                hideLoading();
                Toast.makeText(context, "Network error", Toast.LENGTH_LONG).show();
                if (onFail != null) onFail.run();
            }
        });
    }
    protected void resendVerificationEmail(Context context) {
        resendVerificationEmail(context, null, null);
    }
    protected void saveLastSentTime(Context context) {
        long now = System.currentTimeMillis();
        context.getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                .edit()
                .putLong(PREF_KEY_LAST_SENT, now)
                .apply();
    }

    protected long getCooldownTimeLeft(Context context) {
        long lastSent = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                .getLong(PREF_KEY_LAST_SENT, 0);
        long now = System.currentTimeMillis();
        return Math.max(0, COOLDOWN_MS - (now - lastSent));
    }
    protected void startCooldown(Context context, Button targetButton) {
        long timeLeft = getCooldownTimeLeft(context);

        if (timeLeft <= 0) {
            targetButton.setEnabled(true);
            targetButton.setText("Gửi lại");
            return;
        }

        targetButton.setEnabled(false);

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                targetButton.setText("Gửi lại sau " + seconds + "s");
            }

            public void onFinish() {
                targetButton.setEnabled(true);
                targetButton.setText("Gửi lại");
            }
        }.start();
    }
}
