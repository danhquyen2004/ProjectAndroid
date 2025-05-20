package com.example.tlupickleball.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tlupickleball.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        checkProfileStatus(); // ✅ kiểm tra hồ sơ ngay khi mở màn hình
    }

    private void checkProfileStatus() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();
        db.collection("users").document(uid).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String name = document.getString("name");
                        String dob = document.getString("dob");
                        String gender = document.getString("gender");

                        if (name == null || name.isEmpty() ||
                                dob == null || dob.isEmpty() ||
                                gender == null || gender.isEmpty()) {
                            // ✅ Thiếu thông tin → chuyển sang ProfileActivity
                            Intent intent = new Intent(this, ProfileActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // ✅ Đã có đầy đủ hồ sơ → tiếp tục vào MainActivity
                            Toast.makeText(this, "Chào mừng " + name, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Không tồn tại user → không hợp lệ
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi kiểm tra hồ sơ", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }
}
