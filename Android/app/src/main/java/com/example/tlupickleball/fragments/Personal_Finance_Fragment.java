package com.example.tlupickleball.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tlupickleball.R;
import com.example.tlupickleball.activities.Activity_payment;
import com.example.tlupickleball.adapters.Transaction_PersonalAdapter;
import com.example.tlupickleball.model.MemberFund1;
import com.example.tlupickleball.model.logs;
import com.example.tlupickleball.network.api_model.finance.FinanceListResponse;
import com.example.tlupickleball.network.core.ApiClient;
import com.example.tlupickleball.network.core.SessionManager;
import com.example.tlupickleball.network.service.FinanceService;
import com.example.tlupickleball.network.service.UserService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.tlupickleball.model.User; // Add this import
import com.bumptech.glide.Glide; // Add this import


public class Personal_Finance_Fragment extends Fragment {

    private com.google.android.material.imageview.ShapeableImageView avatar;
    private TextView tvName, tvFund, tvDonate, txTotalPenalty, txTotalPenaltyUnpaid, txTotalPenaltyPaid;
    private View rootView, contentContainer;
    private LinearLayout btnClick;
    private Spinner spinnerMonth;
    List<String> monthList = new ArrayList<>();
    int year = Calendar.getInstance().get(Calendar.YEAR);
    private UserService userService;
    private FinanceService financeService;
    private List<logs> logList = new ArrayList<>();
    private List<logs> filteredList = new ArrayList<>();
    private RecyclerView rvTransactions_Personal;
    private Transaction_PersonalAdapter transactionPersonalAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_personal_finance, container, false);
        financeService = ApiClient.getClient(requireContext()).create(FinanceService.class);

        contentContainer = rootView.findViewById(R.id.contentContainerHome);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        progressBar = rootView.findViewById(R.id.progressBarHome);

        initViews();
        setupListeners();
        setupMonthSpinner();
        setupRecyclerView();
        loadUserInfo(SessionManager.getUid(requireContext()));
        loadFinanceStatus(SessionManager.getUid(requireContext()), Calendar.getInstance().get(Calendar.MONTH) + 1, year);

        swipeRefreshLayout.setOnRefreshListener(this::loadInfomation);
        return rootView;
    }

    private void initViews() {
        avatar = rootView.findViewById(R.id.image_avatar);
        tvName = rootView.findViewById(R.id.tvName);
        tvFund = rootView.findViewById(R.id.id_fund);
        tvDonate = rootView.findViewById(R.id.id_donate);
        txTotalPenalty = rootView.findViewById(R.id.id_penalty);
        txTotalPenaltyUnpaid = rootView.findViewById(R.id.id_penalty_missing);
        txTotalPenaltyPaid = rootView.findViewById(R.id.id_penalty_done);
        btnClick = rootView.findViewById(R.id.btn_click_pay);
    }

    private void setupListeners(){
        btnClick.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), Activity_payment.class);
            startActivity(intent);
        });
    }

    private void setupMonthSpinner() {
        spinnerMonth = rootView.findViewById(R.id.spinnerMonth);

        monthList.clear(); // Xóa trước nếu gọi lại nhiều lần

        for (int i = 1; i <= 12; i++) {
            monthList.add("Tháng " + i + "/" + year);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                monthList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);

        // Set tháng hiện tại
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        spinnerMonth.setSelection(currentMonth);

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Lấy tháng được chọn từ Spinner
                String selectedMonthStr = parent.getItemAtPosition(position).toString();
                // Tách tháng và năm từ chuỗi "Tháng x/yyyy"
                int selectedMonthNumber = Integer.parseInt(selectedMonthStr.split(" ")[1].split("/")[0]);
                int selectedYear = year;

                // Gọi API với tháng và năm đã chọn
                loadFinanceStatus(SessionManager.getUid(requireContext()), selectedMonthNumber, selectedYear);
                // Lọc giao dịch theo tháng đã chọn
                filterTransactionsByMonth(selectedMonthNumber);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không chọn gì
            }
        });
    }

    private void setupRecyclerView() {
        rvTransactions_Personal = rootView.findViewById(R.id.rvTransaction_Personal);
        rvTransactions_Personal.setLayoutManager(new LinearLayoutManager(requireContext()));
        transactionPersonalAdapter = new Transaction_PersonalAdapter(requireContext(), logList);
        rvTransactions_Personal.setAdapter(transactionPersonalAdapter);
    }

    private void filterTransactionsByMonth(int month) {
        for (logs log : logList) {
            // Giả sử createdAt có định dạng dd/MM/yyyy
            String[] parts = log.getCreatedAt().split("/");
            if (parts.length >= 2) {
                int transactionMonth = Integer.parseInt(parts[1]);
                if (transactionMonth == month) {
                    filteredList.add(log);
                }
            }
        }
        transactionPersonalAdapter.setData(filteredList);
    }

    private void loadUserInfo(String userId) {
        if (!swipeRefreshLayout.isRefreshing()) {
            if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
            if (contentContainer != null) contentContainer.setVisibility(View.INVISIBLE); // Dùng INVISIBLE để layout không bị giật
        }
        userService = ApiClient.getClient(requireContext()).create(UserService.class);
        userService.getUserProfileById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    tvName.setText(user.getFullName());
                    Glide.with(requireContext())
                            .load(user.getAvatarUrl())
                            .placeholder(R.drawable.avatar_1)
                            .into(avatar);
                    loadFinanceStatus(SessionManager.getUid(requireContext()), Calendar.getInstance().get(Calendar.MONTH) + 1, year);
                } else {
                    Toast.makeText(requireContext(), "Không thể tải thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFinanceStatus(String userId, int month, int year) {
        financeService.financeStatus(userId, month, year).enqueue(new Callback<MemberFund1>() {
            @Override
            public void onResponse(Call<MemberFund1> call, Response<MemberFund1> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MemberFund1 data = response.body();

                    // Quỹ: đỏ nếu chưa đóng, xanh nếu đã đóng
                    if ("paid".equalsIgnoreCase(data.getFixedFund().getStatus())) {
                        tvFund.setBackgroundResource(R.drawable.button_light_green_bg);
                        tvFund.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_Dong));
                    } else {
                        tvFund.setBackgroundResource(R.drawable.button_light_red_bg);
                        tvFund.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_Dong));
                    }
                    tvFund.setText("Quỹ: " + formatCurrency(data.getFixedFund().getAmount()));

                    // Ủng hộ: luôn màu xanh
                    tvDonate.setBackgroundResource(R.drawable.button_light_green_bg);
                    tvDonate.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_Dong));
                    tvDonate.setText("Ủng hộ: " + formatCurrency(data.getTotalDonation()));

                    // Đóng phạt
                    long total = data.getTotalPenalty();
                    long paid = data.getTotalPenaltyPaid();
                    long unpaid = data.getTotalPenaltyUnpaid();

                    if (total == paid) {
                        // Tất cả đều màu xanh
                        txTotalPenalty.setBackgroundResource(R.drawable.button_light_green_bg);
                        txTotalPenalty.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_Dong));

                        txTotalPenaltyPaid.setBackgroundResource(R.drawable.button_light_green_bg);
                        txTotalPenaltyPaid.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_Dong));

                        txTotalPenaltyUnpaid.setBackgroundResource(R.drawable.button_light_green_bg);
                        txTotalPenaltyUnpaid.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_Dong));
                    } else if (total == unpaid) {
                        // Tổng và còn thiếu màu đỏ, đã đóng màu xanh
                        txTotalPenalty.setBackgroundResource(R.drawable.button_light_red_bg);
                        txTotalPenalty.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_Dong));

                        txTotalPenaltyUnpaid.setBackgroundResource(R.drawable.button_light_red_bg);
                        txTotalPenaltyUnpaid.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_Dong));

                        txTotalPenaltyPaid.setBackgroundResource(R.drawable.button_light_green_bg);
                        txTotalPenaltyPaid.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_Dong));
                    } else {
                        // Tổng vàng, đã đóng xanh, còn thiếu đỏ
                        txTotalPenalty.setBackgroundResource(R.drawable.button_light_yellow_bg);
                        txTotalPenalty.setTextColor(ContextCompat.getColor(requireContext(), R.color.yellow_Dong));

                        txTotalPenaltyPaid.setBackgroundResource(R.drawable.button_light_green_bg);
                        txTotalPenaltyPaid.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_Dong));

                        txTotalPenaltyUnpaid.setBackgroundResource(R.drawable.button_light_red_bg);
                        txTotalPenaltyUnpaid.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_Dong));
                    }

                    txTotalPenalty.setText("Tổng: " + formatCurrency(total));
                    txTotalPenaltyUnpaid.setText("Còn thiếu: " + formatCurrency(unpaid));
                    txTotalPenaltyPaid.setText("Đã đóng: " + formatCurrency(paid));

                    Toast.makeText(requireContext(), "Tải dữ liệu thành công", Toast.LENGTH_SHORT).show();
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    if (contentContainer != null) contentContainer.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    Toast.makeText(requireContext(), "Không thể tải dữ liệu tài chính", Toast.LENGTH_SHORT).show();
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    if (contentContainer != null) contentContainer.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<MemberFund1> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Gọi API lấy danh sách giao dịch
        financeService.financeHistory(userId, month, year).enqueue(new Callback<FinanceListResponse>() {
            @Override
            public void onResponse(Call<FinanceListResponse> call, Response<FinanceListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Use logs list directly
                    logList = response.body().getLogs();
                    transactionPersonalAdapter.setData(logList); // Adapter should accept List<logs>
                }
            }
            @Override
            public void onFailure(Call<FinanceListResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi tải giao dịch: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadInfomation(){
        loadUserInfo(SessionManager.getUid(requireContext()));
        loadFinanceStatus(SessionManager.getUid(requireContext()), Calendar.getInstance().get(Calendar.MONTH) + 1, year);;
    }
    private String formatCurrency(long amount) {
        return String.format("%,d", amount).replace(',', '.') + "đ";
    }
}