package com.example.tlupickleball.activities;

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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tlupickleball.R;
import com.example.tlupickleball.adapters.Transaction_PersonalAdapter;
import com.example.tlupickleball.model.MemberFund1;
import com.example.tlupickleball.model.User;
import com.example.tlupickleball.model.logs;
import com.example.tlupickleball.network.api_model.finance.FinanceListResponse;
import com.example.tlupickleball.network.core.ApiClient;
import com.example.tlupickleball.network.service.FinanceService;
import com.example.tlupickleball.network.service.UserService;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class member_fundpersonal extends AppCompatActivity {

    private String userId; // Thêm biến này
    private TextView tvUserNamePersonal, tvUserFundPersonal, tvUserDonatePersonal, tvUserPenaltyTotalPersonal, tvUserPenaltyPaidPersonal, tvUserPenaltyMissingPersonal;
    private com.google.android.material.imageview.ShapeableImageView ivUserAvatarPersonal;
    private ImageButton btnBack;
    private Spinner spinnerMonth;
    private final List<String> monthList = new ArrayList<>();
    private final int year_FundMember = Calendar.getInstance().get(Calendar.YEAR);
    private RecyclerView rvTransactions_Personal;
    private Transaction_PersonalAdapter transactionFundPersonalAdapter;
    private List<logs> logList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_member_fundpersonal);

        // Nhận userId từ Intent
        userId = getIntent().getStringExtra("USER_ID");
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy ID người dùng", Toast.LENGTH_SHORT).show();
            finish(); // Đóng Activity nếu không có ID
            return;
        }

        initViews();
        setupListener();
        setupSpinner();
        setupRecyclerView();
        fetchUserDetails(userId); // Gọi API lấy thông tin chi tiết của User

        // Gọi API lấy log giao dịch của user
        fetchUserLogs();
    }

    private void initViews() {
        btnBack = findViewById(R.id.id_back_donate_manager);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        tvUserNamePersonal = findViewById(R.id.tvName_personal);
        tvUserFundPersonal = findViewById(R.id.id_fund_personal);
        tvUserDonatePersonal = findViewById(R.id.id_donate_personal);
        tvUserPenaltyTotalPersonal = findViewById(R.id.id_penalty_personal);
        tvUserPenaltyPaidPersonal = findViewById(R.id.id_penalty_done_personal);
        tvUserPenaltyMissingPersonal = findViewById(R.id.id_penalty_missing_personal);
        ivUserAvatarPersonal = findViewById(R.id.image_avatar_member_fundpersonal); // Ánh xạ ShapeableImageView
        rvTransactions_Personal = findViewById(R.id.rvTransaction_Personal);
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

    private void setupSpinner() {
        for (int i = 1; i <= 12; i++) {
            monthList.add("Tháng " + i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, monthList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);

        // Đặt tháng hiện tại
        spinnerMonth.setSelection(Calendar.getInstance().get(Calendar.MONTH));

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedMonth = position + 1;
                filterTransactionsByMonth(selectedMonth);
                fetchUserFundStatus(userId, selectedMonth, year_FundMember); // Cập nhật quỹ theo tháng
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
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
        if (logList != null) {
            for (logs log : logList) {
                String[] parts = log.getCreatedAt().split("/");
                if (parts.length >= 2) {
                    int transactionMonth = Integer.parseInt(parts[1]);
                    if (transactionMonth == month) {
                        filteredList.add(log);
                    }
                }
            }
        }
        if (transactionFundPersonalAdapter != null) {
            transactionFundPersonalAdapter.setData(filteredList);
        }
        fetchUserLogsForMonth(month, year_FundMember);
    }

    private void setupRecyclerView() {
        rvTransactions_Personal.setLayoutManager(new LinearLayoutManager(this));
        logList = new ArrayList<>();
        transactionFundPersonalAdapter = new Transaction_PersonalAdapter(this, logList);
        rvTransactions_Personal.setAdapter(transactionFundPersonalAdapter);
    }

    // Hàm lấy log giao dịch của user từ server
    // Hàm mới để lấy thông tin chi tiết của User (tên, ảnh)
    private void fetchUserDetails(String userId) {
        UserService userService = ApiClient.getClient(this).create(UserService.class);
        Call<User> call = userService.getUserProfileById(userId); // Giả sử có API getUserById
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    tvUserNamePersonal.setText(user.getFullName());
                    if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                        Glide.with(member_fundpersonal.this)
                                .load(user.getAvatarUrl())
                                .placeholder(R.drawable.avatar_1) // Ảnh mặc định
                                .error(R.drawable.avatar_1) // Ảnh lỗi
                                .into(ivUserAvatarPersonal);
                    } else {
                        ivUserAvatarPersonal.setImageResource(R.drawable.avatar_1); // Nếu không có avatar
                    }
                } else {
                    Toast.makeText(member_fundpersonal.this, "Không thể tải thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(member_fundpersonal.this, "Lỗi tải thông tin người dùng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm mới để lấy trạng thái quỹ của user
    private void fetchUserFundStatus(String userId, int month, int year) {
        FinanceService financeService = ApiClient.getClient(this).create(FinanceService.class);
        Call<MemberFund1> call = financeService.financeStatus(userId, month, year);
        call.enqueue(new Callback<MemberFund1>() {
            @Override
            public void onResponse(Call<MemberFund1> call, Response<MemberFund1> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MemberFund1 data = response.body();

                    // Quỹ: đỏ nếu chưa đóng, xanh nếu đã đóng
                    if ("paid".equalsIgnoreCase(data.getFixedFund().getStatus())) {
                        tvUserFundPersonal.setBackgroundResource(R.drawable.button_light_green_bg);
                        tvUserFundPersonal.setTextColor(ContextCompat.getColor(member_fundpersonal.this, R.color.green_Dong));
                    } else {
                        tvUserFundPersonal.setBackgroundResource(R.drawable.button_light_red_bg);
                        tvUserFundPersonal.setTextColor(ContextCompat.getColor(member_fundpersonal.this, R.color.red_Dong));
                    }
                    tvUserFundPersonal.setText("Quỹ: " + formatCurrency(data.getFixedFund().getAmount()));

                    // Ủng hộ: luôn màu xanh
                    tvUserDonatePersonal.setBackgroundResource(R.drawable.button_light_green_bg);
                    tvUserDonatePersonal.setTextColor(ContextCompat.getColor(member_fundpersonal.this, R.color.green_Dong));
                    tvUserDonatePersonal.setText("Ủng hộ: " + formatCurrency(data.getTotalDonation()));

                    // Đóng phạt
                    long total = data.getTotalPenalty();
                    long paid = data.getTotalPenaltyPaid();
                    long unpaid = data.getTotalPenaltyUnpaid();

                    if (total == paid) {
                        // Tất cả đều màu xanh
                        tvUserPenaltyTotalPersonal.setBackgroundResource(R.drawable.button_light_green_bg);
                        tvUserPenaltyTotalPersonal.setTextColor(ContextCompat.getColor(member_fundpersonal.this, R.color.green_Dong));

                        tvUserPenaltyPaidPersonal.setBackgroundResource(R.drawable.button_light_green_bg);
                        tvUserPenaltyPaidPersonal.setTextColor(ContextCompat.getColor(member_fundpersonal.this, R.color.green_Dong));

                        tvUserPenaltyPaidPersonal.setBackgroundResource(R.drawable.button_light_green_bg);
                        tvUserPenaltyPaidPersonal.setTextColor(ContextCompat.getColor(member_fundpersonal.this, R.color.green_Dong));
                    } else if (total == unpaid) {
                        // Tổng và còn thiếu màu đỏ, đã đóng màu xanh
                        tvUserPenaltyTotalPersonal.setBackgroundResource(R.drawable.button_light_red_bg);
                        tvUserPenaltyTotalPersonal.setTextColor(ContextCompat.getColor(member_fundpersonal.this, R.color.red_Dong));

                        tvUserPenaltyMissingPersonal.setBackgroundResource(R.drawable.button_light_red_bg);
                        tvUserPenaltyMissingPersonal.setTextColor(ContextCompat.getColor(member_fundpersonal.this, R.color.red_Dong));

                        tvUserPenaltyPaidPersonal.setBackgroundResource(R.drawable.button_light_green_bg);
                        tvUserPenaltyPaidPersonal.setTextColor(ContextCompat.getColor(member_fundpersonal.this, R.color.green_Dong));
                    } else {
                        // Tổng vàng, đã đóng xanh, còn thiếu đỏ
                        tvUserPenaltyTotalPersonal.setBackgroundResource(R.drawable.button_light_yellow_bg);
                        tvUserPenaltyTotalPersonal.setTextColor(ContextCompat.getColor(member_fundpersonal.this, R.color.yellow_Dong));

                        tvUserPenaltyPaidPersonal.setBackgroundResource(R.drawable.button_light_green_bg);
                        tvUserPenaltyPaidPersonal.setTextColor(ContextCompat.getColor(member_fundpersonal.this, R.color.green_Dong));

                        tvUserPenaltyMissingPersonal.setBackgroundResource(R.drawable.button_light_red_bg);
                        tvUserPenaltyMissingPersonal.setTextColor(ContextCompat.getColor(member_fundpersonal.this, R.color.red_Dong));
                    }

                    tvUserPenaltyTotalPersonal.setText("Tổng: " + formatCurrency(total));
                    tvUserPenaltyMissingPersonal.setText("Còn thiếu: " + formatCurrency(unpaid));
                    tvUserPenaltyPaidPersonal.setText("Đã đóng: " + formatCurrency(paid));

                    Toast.makeText(member_fundpersonal.this, "Tải dữ liệu thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(member_fundpersonal.this, "Không thể tải dữ liệu tài chính", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MemberFund1> call, Throwable t) {
                Toast.makeText(member_fundpersonal.this, "Lỗi tải quỹ: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Hàm lấy log giao dịch của user từ server (đã có, chỉ cần đảm bảo gọi đúng)
    private void fetchUserLogs() {
        FinanceService financeService = ApiClient.getClient(this).create(FinanceService.class);
        // Lấy tháng và năm hiện tại để tải logs ban đầu
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int year = Calendar.getInstance().get(Calendar.YEAR);
        Call<FinanceListResponse> call = financeService.financeHistory(userId, month, year);
        call.enqueue(new Callback<FinanceListResponse>() {
            @Override
            public void onResponse(Call<FinanceListResponse> call, Response<FinanceListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    logList = response.body().getLogs();
                    filterTransactionsByMonth(month); // Lọc theo tháng hiện tại
                } else {
                    Toast.makeText(member_fundpersonal.this, "Không có dữ liệu giao dịch", Toast.LENGTH_SHORT).show();
                    logList.clear(); // Xóa dữ liệu cũ nếu không có dữ liệu mới
                    transactionFundPersonalAdapter.setData(logList); // Cập nhật Adapter
                }
            }
            @Override
            public void onFailure(Call<FinanceListResponse> call, Throwable t) {
                Toast.makeText(member_fundpersonal.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                logList.clear(); // Xóa dữ liệu cũ nếu có lỗi
                transactionFundPersonalAdapter.setData(logList); // Cập nhật Adapter
            }
        });
    }

    // Hàm mới để lấy logs theo tháng và năm cụ thể
    private void fetchUserLogsForMonth(int month, int year) {
        FinanceService financeService = ApiClient.getClient(this).create(FinanceService.class);
        Call<FinanceListResponse> call = financeService.financeHistory(userId, month, year);
        call.enqueue(new Callback<FinanceListResponse>() {
            @Override
            public void onResponse(Call<FinanceListResponse> call, Response<FinanceListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    logList = response.body().getLogs();
                    transactionFundPersonalAdapter.setData(logList); // Cập nhật Adapter với dữ liệu mới
                } else {
                    Toast.makeText(member_fundpersonal.this, "Không có dữ liệu giao dịch cho tháng này", Toast.LENGTH_SHORT).show();
                    logList.clear();
                    transactionFundPersonalAdapter.setData(logList);
                }
            }
            @Override
            public void onFailure(Call<FinanceListResponse> call, Throwable t) {
                Toast.makeText(member_fundpersonal.this, "Lỗi tải giao dịch: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                logList.clear();
                transactionFundPersonalAdapter.setData(logList);
            }
        });
    }


    private String formatCurrency(long amount) {
        NumberFormat format = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        return format.format(amount) + "đ";
    }
}