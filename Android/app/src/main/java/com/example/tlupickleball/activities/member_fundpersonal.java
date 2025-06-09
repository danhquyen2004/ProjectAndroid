package com.example.tlupickleball.activities;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tlupickleball.R;

public class member_fundpersonal extends AppCompatActivity {

    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_member_fundpersonal);

        initViews();
        setupListener();
    }

    private void initViews() {
        btnBack = findViewById(R.id.id_back_donate_manager);

    }

    private void setupListener() {
        btnBack.setOnClickListener(v -> finish());
    }
}