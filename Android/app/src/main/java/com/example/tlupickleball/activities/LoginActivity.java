package com.example.tlupickleball.activities;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    EditText edtPhone, edtPassword;
    Button btnLogin, btnRegister;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> {
            String phone = edtPhone.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            loginWithPhoneAndPassword(phone, password);
        });
    }

    private void loginWithPhoneAndPassword(String phone, String password) {
        db.collection("users")
                .whereEqualTo("phoneNumber", phone)
                .get()
                .addOnSuccessListener(query -> {
                    if (query.isEmpty()) {
                        Toast.makeText(this, "Không tìm thấy tài khoản", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    DocumentSnapshot userDoc = query.getDocuments().get(0);
                    String savedPassword = userDoc.getString("password");

                    if (!password.equals(savedPassword)) {
                        Toast.makeText(this, "Sai mật khẩu", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // ✅ Cho vào app nếu đúng mật khẩu
                    // Có thể kiểm tra approved nếu muốn tại đây
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi truy vấn Firestore", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }
}
