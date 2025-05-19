package com.example.tlupickleball.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tlupickleball.R;
import com.example.tlupickleball.apis.UserApi;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {
    EditText edtName, edtDob, edtGender;
    Button btnSubmit;
    String idToken;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        edtName = findViewById(R.id.edtName);
        edtDob = findViewById(R.id.edtDob);
        edtGender = findViewById(R.id.edtGender);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Lấy ID Token từ Firebase
        FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                .addOnSuccessListener(result -> idToken = result.getToken());

        btnSubmit.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String dob = edtDob.getText().toString().trim();
            String gender = edtGender.getText().toString().trim();

            if (name.isEmpty() || dob.isEmpty() || gender.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            UserApi.sendSubmitProfile(this, idToken, name, dob, gender);
        });
    }
}
