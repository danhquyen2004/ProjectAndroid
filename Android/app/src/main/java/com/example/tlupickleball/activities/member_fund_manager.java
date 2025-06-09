package com.example.tlupickleball.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlupickleball.R;
import com.example.tlupickleball.adapters.MemberFundAdapter;
import com.example.tlupickleball.model.MemberFund;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class member_fund_manager extends AppCompatActivity {
    private ImageButton btnBack;
    private ConstraintLayout CtLMemberFund;
    private RecyclerView rvMemberFund;
    private Spinner spinnerMonth;
    private MemberFundAdapter adapter;
    private List<MemberFund> memberList = new ArrayList<>();
    private List<String> monthList = new ArrayList<>();
    private int selectedMonthNumber;
    private final int year = Calendar.getInstance().get(Calendar.YEAR);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_member_fund_manager);

        initViews();
        setupClickListeners();
        setupMonthSpinner();
        setupRecyclerView();
    }

    private void initViews() {
        btnBack = findViewById(R.id.id_back_donate_manager);
        CtLMemberFund = findViewById(R.id.CtLMemberFund);
        rvMemberFund = findViewById(R.id.rvMemberFund);
        spinnerMonth = findViewById(R.id.spinnerMonth);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

    }

    private void setupMonthSpinner() {
        generateMonthList();

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
                String selectedMonth = monthList.get(position);
                selectedMonthNumber = Integer.parseInt(selectedMonth.split(" ")[1].split("/")[0]);
                updateDataForMonth(selectedMonthNumber);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void generateMonthList() {
        monthList.clear();
        for (int i = 1; i <= 12; i++) {
            monthList.add("Tháng " + i + "/" + year);
        }
    }

    private void setupRecyclerView() {
        adapter = new MemberFundAdapter(this, new ArrayList<>());
        rvMemberFund.setLayoutManager(new LinearLayoutManager(this));
        rvMemberFund.setAdapter(adapter);

        updateDataForMonth(Calendar.getInstance().get(Calendar.MONTH) + 1); // Load mặc định
    }

    private void updateDataForMonth(int month) {
        memberList = generateSampleData(month);
        adapter.setData(memberList);
    }

    private List<MemberFund> generateSampleData(int month) {
        List<MemberFund> list = new ArrayList<>();
        list.add(new MemberFund("Nguyễn Văn A", 100_000, 50_000, 100_000));
        list.add(new MemberFund("Nguyễn Giang Đông", 100_000, 50_000, 100_000));
        list.add(new MemberFund("Nguyễn Danh Quyền", 100_000, 50_000, 100_000));
        list.add(new MemberFund("Chu Mạnh Hữu", 100_000, 50_000, 100_000));
        list.add(new MemberFund("Phạm Đỗ Anh", 100_000, 50_000, 100_000));
        list.add(new MemberFund("Trần Thị B", 0, 0, 100_000)); // Chưa đóng
        return list;
    }
}