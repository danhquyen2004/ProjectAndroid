package com.example.tlupickleball.activities;

import android.content.Intent;
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
import com.example.tlupickleball.adapters.TransactionAdapter;
import com.example.tlupickleball.model.Transaction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Activity_payment extends AppCompatActivity {

    private ImageButton btnBack;
    private Spinner spinnerMonth;
    private TextView tvFundAmount, tvPenaltyAmount;
    private Button btnPayFund, btnPayPenalty;
    private LinearLayout btnDonate;

    private final List<String> monthList = new ArrayList<>();
    private final int year = Calendar.getInstance().get(Calendar.YEAR);

    private List<Transaction> transactionList = new ArrayList<>();
    private TransactionAdapter transactionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment);

        initViews();
        setupClickListeners();
        setupMonthSpinner();
    }

    // Khởi tạo các View trong layout
    private void initViews() {
        btnBack = findViewById(R.id.id_back);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        tvFundAmount = findViewById(R.id.tvFundAmount);
        tvPenaltyAmount = findViewById(R.id.tvPenaltyAmount);
        btnPayFund = findViewById(R.id.btnPayFund);
        btnPayPenalty = findViewById(R.id.btnPayPenalty);
        btnDonate = findViewById(R.id.btnDonate);
    }

    // Thiết lập các sự kiện click
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

    // Hiển thị Toast tiện dụng
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Tạo danh sách các tháng trong năm hiện tại
    private void populateMonthList() {
        monthList.clear();
        for (int i = 1; i <= 12; i++) {
            monthList.add("Tháng " + i + "/" + year);
        }
    }

    // Cài đặt Spinner để chọn tháng
    private void setupMonthSpinner() {
        populateMonthList();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                monthList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);

        // Thiết lập tháng hiện tại
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH); // 0-11
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

    // Tách số tháng từ chuỗi dạng "Tháng x/yyyy"
    private int parseMonthFromString(String monthText) {
        try {
            return Integer.parseInt(monthText.split(" ")[1].split("/")[0]);
        } catch (Exception e) {
            return Calendar.getInstance().get(Calendar.MONTH) + 1;
        }
    }

    // Lọc danh sách giao dịch theo tháng
    private void filterTransactionsByMonth(int month) {
        List<Transaction> filteredList = new ArrayList<>();

        for (Transaction transaction : transactionList) {
            // Giả sử định dạng ngày là "dd/MM/yyyy"
            String[] parts = transaction.getTime().split("/");
            if (parts.length >= 2) {
                int transactionMonth = Integer.parseInt(parts[1]);
                if (transactionMonth == month) {
                    filteredList.add(transaction);
                }
            }
        }

        if (transactionAdapter != null) {
            transactionAdapter.setData(filteredList);
        }
    }

    // tạo qr code
    private void showQrPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.qr_finance, null);

        ImageView imageQr = dialogView.findViewById(R.id.image_qr);
        imageQr.setImageResource(R.drawable.qr_code); // đặt QR mẫu ở drawable

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Làm trong suốt background nếu cần
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialog.show();
    }
}
