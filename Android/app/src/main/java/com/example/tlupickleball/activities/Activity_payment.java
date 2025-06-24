package com.example.tlupickleball.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tlupickleball.R;
import com.example.tlupickleball.adapters.Transaction_PersonalAdapter;
import com.example.tlupickleball.model.logs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Activity_payment extends AppCompatActivity {

    private ImageButton btnBack;
    private Spinner spinnerMonth;
    private TextView tvFundAmount, tvPenaltyAmount, tvContentQr;
    private Button btnPayFund, btnPayPenalty;
    private LinearLayout btnDonate;

    private final List<String> monthList = new ArrayList<>();
    private final int year = Calendar.getInstance().get(Calendar.YEAR);

    private List<logs> logList = new ArrayList<>();
    private Transaction_PersonalAdapter transactionPersonalAdapter;
    ImageView imageQr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment);

        initViews();
        setupClickListeners();
        setupMonthSpinner();

    }

    private void initViews() {
        btnBack = findViewById(R.id.id_back);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        tvFundAmount = findViewById(R.id.tvFundAmount);
        tvPenaltyAmount = findViewById(R.id.tvPenaltyAmount);
        btnPayFund = findViewById(R.id.btnPayFund);
        btnPayPenalty = findViewById(R.id.btnPayPenalty);
        btnDonate = findViewById(R.id.btnDonate);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnPayFund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQrPopup();
                showToast("Đã đóng quỹ cố định");
            }
        });

        btnPayPenalty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQrPopup();
                showToast("Đã đóng phạt trận thua");
            }
        });

        btnDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_payment.this, donate.class);
                startActivity(intent);
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void populateMonthList() {
        monthList.clear();
        for (int i = 1; i <= 12; i++) {
            monthList.add("Tháng " + i + "/" + year);
        }
    }

    private void setupMonthSpinner() {
        populateMonthList();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                monthList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);

        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        spinnerMonth.setSelection(currentMonth);

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMonth = parent.getItemAtPosition(position).toString();
                int selectedMonthNumber = parseMonthFromString(selectedMonth);
                filterTransactionsByMonth(selectedMonthNumber);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private int parseMonthFromString(String monthText) {
        try {
            return Integer.parseInt(monthText.split(" ")[1].split("/")[0]);
        } catch (Exception e) {
            return Calendar.getInstance().get(Calendar.MONTH) + 1;
        }
    }

    private void filterTransactionsByMonth(int month) {
        List<logs> filteredList = new ArrayList<>();

        for (logs log : logList) {
            // Giả sử định dạng ngày là "dd/MM/yyyy"
            String[] parts = log.getCreatedAt().split("/");
            if (parts.length >= 2) {
                int transactionMonth = Integer.parseInt(parts[1]);
                if (transactionMonth == month) {
                    filteredList.add(log);
                }
            }
        }

        if (transactionPersonalAdapter != null) {
            transactionPersonalAdapter.setData(filteredList);
        }
    }

    private void showQrPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.qr_finance, null);

        imageQr = dialogView.findViewById(R.id.image_qr);
        imageQr.setImageResource(R.drawable.qr_code);

        tvContentQr = dialogView.findViewById(R.id.txtContent_Donate);
        tvContentQr.setText("CHÚ Ý:\nNội dung chuyển khoản: Họ Tên + Đã đóng");
        tvContentQr.setTextColor(Color.parseColor("#FF0000"));

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialog.show();
    }
}
