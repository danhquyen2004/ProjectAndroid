package com.example.tlupickleball.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tlupickleball.R;

public class donate extends AppCompatActivity {

    ImageButton btnBack;
    Button btnDonateDone;
    EditText edtDonateInput;
    TextView txtContentDonate;
    ImageView imageQr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_donate);

        initViews();
        setOnClick();

    }

    private void initViews() {
        btnBack = findViewById(R.id.id_back_donate);
        btnDonateDone = findViewById(R.id.btn_donate_done);
        edtDonateInput = findViewById(R.id.edtDonate);
    }

    private void setOnClick() {
        btnBack.setOnClickListener(v -> finish());
        btnDonateDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQrPopup();
                showToast("Đã ủng hộ thành công");
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showQrPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.qr_finance, null);

        imageQr = dialogView.findViewById(R.id.image_qr);
        imageQr.setImageResource(R.drawable.qr_code);

        txtContentDonate = dialogView.findViewById(R.id.txtContent_Donate);
        txtContentDonate.setText("Hãy ủng hộ câu lạc bộ chúng tôi");
        txtContentDonate.setTextColor(Color.parseColor("#00CF00"));
        txtContentDonate.setTextSize(20);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Làm trong suốt background nếu cần
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialog.show();
    }
}