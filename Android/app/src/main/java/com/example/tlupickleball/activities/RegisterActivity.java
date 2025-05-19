package com.example.tlupickleball.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tlupickleball.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {
    EditText edtPhone, edtPassword, edtConfirm;
    Button btnSendOtp;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirm = findViewById(R.id.edtConfirm);
        btnSendOtp = findViewById(R.id.btnSendOtp);

        btnSendOtp.setOnClickListener(v -> {
            String phone = edtPhone.getText().toString().trim();
            String pw = edtPassword.getText().toString().trim();
            String confirm = edtConfirm.getText().toString().trim();

            if (phone.isEmpty() || pw.isEmpty() || !pw.equals(confirm)) {
                Toast.makeText(this, "Thông tin không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            sendOtp(phone,pw);
        });
    }

    private void sendOtp(String phone, String password) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {}

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Toast.makeText(RegisterActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                        Intent intent = new Intent(RegisterActivity.this, OtpActivity.class);
                        intent.putExtra("phone", phone);
                        intent.putExtra("password",password ); // ✅ truyền mật khẩu
                        intent.putExtra("verificationId", verificationId);
                        startActivity(intent);

                    }
                }).build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}
