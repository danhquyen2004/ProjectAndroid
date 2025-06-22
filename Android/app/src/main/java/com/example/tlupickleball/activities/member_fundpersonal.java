package com.example.tlupickleball.activities;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlupickleball.R;
import com.example.tlupickleball.adapters.Transaction_PersonalAdapter;
import com.example.tlupickleball.model.Transaction_Personal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class member_fundpersonal extends AppCompatActivity {

    private ImageButton btnBack;
    private Spinner spinnerMonth;
    private final List<String> monthList = new ArrayList<>();
    private final int year_FundMember = Calendar.getInstance().get(Calendar.YEAR);
    private RecyclerView rvTransactions_Personal;
    private Transaction_PersonalAdapter transactionFundPersonalAdapter;
    private List<Transaction_Personal> transactionFundPersonalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_member_fundpersonal);

        initViews();
        setupListener();
        setupMonthSpinner();
        setupRecyclerView();
    }

    private void initViews() {
        btnBack = findViewById(R.id.id_back_donate_manager);
        spinnerMonth = findViewById(R.id.spinnerMonth);
    }

    private void setupListener() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void populateMonthList() {
        monthList.clear();
        for (int i = 1; i <= 12; i++) {
            monthList.add("Tháng " + i + "/" + year_FundMember);
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
        List<Transaction_Personal> filteredList = new ArrayList<>();

        for (Transaction_Personal transactionPersonal : transactionFundPersonalList) {
            // Giả sử định dạng ngày là "dd/MM/yyyy"
            String[] parts = transactionPersonal.getTime().split("/");
            if (parts.length >= 2) {
                int transactionMonth = Integer.parseInt(parts[1]);
                if (transactionMonth == month) {
                    filteredList.add(transactionPersonal);
                }
            }
        }

        if (transactionFundPersonalAdapter != null) {
            transactionFundPersonalAdapter.setData(filteredList);
        }
    }

    private void setupRecyclerView() {
        rvTransactions_Personal = findViewById(R.id.rvTransaction_Personal);
        rvTransactions_Personal.setLayoutManager(new LinearLayoutManager(this));

        transactionFundPersonalList = new ArrayList<>();

        // Tạo dữ liệu đúng với constructor Transaction(title, amount, time, status, isIncome)
        transactionFundPersonalList.add(new Transaction_Personal("Đóng quỹ", "+100.000đ", "10/04/2025",  true));
        transactionFundPersonalList.add(new Transaction_Personal("Đóng quỹ", "+100.000đ", "10/05/2025",  true));
        transactionFundPersonalList.add(new Transaction_Personal("Đã đóng tiền phạt", "+100.000đ", "11/06/2025",  true));
        transactionFundPersonalList.add(new Transaction_Personal("Đóng quỹ", "+100.000đ", "10/06/2025",  true));
        transactionFundPersonalList.add(new Transaction_Personal("Đóng quỹ", "+100.000đ", "10/03/2025",  true));
        transactionFundPersonalList.add(new Transaction_Personal("Đã đóng tiền phạt", "+100.000đ", "11/05/2025",  true));

        transactionFundPersonalAdapter = new Transaction_PersonalAdapter(this,transactionFundPersonalList);
        rvTransactions_Personal.setAdapter(transactionFundPersonalAdapter);
    }
}