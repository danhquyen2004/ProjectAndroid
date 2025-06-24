package com.example.tlupickleball.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlupickleball.R;
import com.example.tlupickleball.activities.base.BaseActivity;
import com.example.tlupickleball.adapters.MemberFundAdapter;
import com.example.tlupickleball.model.FundStatusAll;
import com.example.tlupickleball.model.MemberFund;
import com.example.tlupickleball.model.User;
import com.example.tlupickleball.network.api_model.finance.FinanceUserFundStatus;
import com.example.tlupickleball.network.api_model.user.UserListResponse;
import com.example.tlupickleball.network.core.ApiClient;
import com.example.tlupickleball.network.service.FinanceService;
import com.example.tlupickleball.network.service.UserService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class member_fund_manager extends BaseActivity {
    private ImageButton btnBack;
    private ConstraintLayout CtLMemberFund;
    private RecyclerView rvMemberFund;
    private Spinner spinnerMonth;
    private MemberFundAdapter adapter;
    private List<FundStatusAll> fundStatusList = new ArrayList<>();
    private List<User> userList = new ArrayList<>();
    private List<String> monthList = new ArrayList<>();
    private int selectedMonthNumber;
    private final int year = Calendar.getInstance().get(Calendar.YEAR);
    private FinanceService financeService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_member_fund_manager);

        initViews();
        setupMonthSpinner();
        setupRecyclerView();
        setupClickListeners();
        fetchUserListAndUpdate(); // Lấy danh sách người dùng từ server
    }

    private void initViews() {
        btnBack = findViewById(R.id.id_back_donate_manager);
        CtLMemberFund = findViewById(R.id.CtLMemberFund);
        rvMemberFund = findViewById(R.id.rvMemberFund);
        spinnerMonth = findViewById(R.id.spinnerMonth);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        adapter.setOnUserClickListener(user -> {
            Intent intent = new Intent(member_fund_manager.this, member_fundpersonal.class);
            intent.putExtra("USER_ID", user.getUid());
            startActivity(intent);
        });
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
        adapter = new MemberFundAdapter(this, new ArrayList<>(), userList);
        rvMemberFund.setLayoutManager(new LinearLayoutManager(this));
        rvMemberFund.setAdapter(adapter);

        updateDataForMonth(Calendar.getInstance().get(Calendar.MONTH) + 1);
    }

    private void updateDataForMonth(int month) {
        financeService = ApiClient.getClient(this).create(FinanceService.class);
        Call<FinanceUserFundStatus> call = financeService.financeStatusAll(month, year);
        call.enqueue(new Callback<FinanceUserFundStatus>() {
            @Override
            public void onResponse(Call<FinanceUserFundStatus> call, Response<FinanceUserFundStatus> response) {
                if (response.isSuccessful() && response.body() != null) {
                    fundStatusList = response.body().getResults();
                    List<FundStatusAll> filteredList = new ArrayList<>();
                    for (FundStatusAll item : fundStatusList) {
                        if (getUserById(item.getUserId()) != null) {
                            filteredList.add(item);
                        }
                    }
                    adapter.setData(filteredList);
                    hideLoading();
                }
            }
            @Override
            public void onFailure(Call<FinanceUserFundStatus> call, Throwable t) {
                Toast.makeText(member_fund_manager.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                hideLoading();
            }
        });
    }

    private User getUserById(String userId) {
        for (User user : userList) {
            if (user.getUid().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    // Thêm hàm lấy userList từ server
    private void fetchUserListAndUpdate() {
        showLoading();
        // Giả sử có UserService và ApiClient đã setup
        UserService userService = ApiClient.getClient(this).create(UserService.class);
        Call<UserListResponse> call = userService.getAllUsers();
        call.enqueue(new Callback<UserListResponse>() {
            @Override
            public void onResponse(Call<UserListResponse> call, Response<UserListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userList.clear();
                    List<User> allUsers = response.body().getUsers();
                    if (allUsers != null) {
                        for (User user : allUsers) {
                            if (user.getFullName() != null && !user.getFullName().trim().isEmpty()) {
                                userList.add(user);
                            }
                        }
                    }
                    // Sau khi có userList, cập nhật lại dữ liệu quỹ
                    updateDataForMonth(selectedMonthNumber);
                }
            }
            @Override
            public void onFailure(Call<UserListResponse> call, Throwable t) {
                Toast.makeText(member_fund_manager.this, "Lỗi tải user: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}