package com.example.tlupickleball.activities;

import static com.example.tlupickleball.apis.UserApi.sendSubmitAccount;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tlupickleball.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OtpActivity extends AppCompatActivity {
    EditText edtOtp;
    Button btnVerify;
    String verificationId, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        edtOtp = findViewById(R.id.edtOtp);
        btnVerify = findViewById(R.id.btnVerify);

        verificationId = getIntent().getStringExtra("verificationId");
        phone = getIntent().getStringExtra("phone");

        btnVerify.setOnClickListener(v -> {
            String otp = edtOtp.getText().toString().trim();
            if (otp.isEmpty()) return;

            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
            FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String phone = getIntent().getStringExtra("phone");
                            String password = getIntent().getStringExtra("password");

                            FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                                    .addOnSuccessListener(result -> {
                                        String idToken = result.getToken();
                                        Log.d("TOKEN", idToken);
                                        sendSubmitAccount(this, idToken, phone, password); // ✅ gọi API
                                    });
                        } else {
                            Toast.makeText(this, "Xác minh thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
