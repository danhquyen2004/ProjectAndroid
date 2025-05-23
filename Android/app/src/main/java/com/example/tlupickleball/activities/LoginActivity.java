package com.example.tlupickleball.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tlupickleball.R;
import com.example.tlupickleball.activities.base.ApiCallback;
import com.example.tlupickleball.activities.base.BaseActivity;
import com.example.tlupickleball.network.auth.AuthApi;
import com.example.tlupickleball.network.core.ApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends BaseActivity {
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView goToRegisterText;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        goToRegisterText = findViewById(R.id.goToRegisterText);

        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(v -> {
            showLoading();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            AuthApi.login(this, email, password, mAuth, new ApiCallback() {
                @Override
                public void onSuccess() {
                    checkUserProfileAndRedirect(LoginActivity.this, mAuth.getCurrentUser());
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onComplete() {
                    hideLoading();
                }
            });
        });

        goToRegisterText.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }
    private static void checkUserProfileAndRedirect(Context context, FirebaseUser user) {
        FirebaseFirestore.getInstance().collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(document -> {
                    if (!document.exists() || !document.contains("name") ||
                            !document.contains("dob") || !document.contains("gender")) {
                        context.startActivity(new Intent(context, ProfileActivity.class));
                    } else {
                        context.startActivity(new Intent(context, MainActivity.class));
                    }
                });
    }
}
