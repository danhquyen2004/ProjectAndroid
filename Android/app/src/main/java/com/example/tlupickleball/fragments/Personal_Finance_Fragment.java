package com.example.tlupickleball.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tlupickleball.R;
import com.example.tlupickleball.activities.Activity_payment;
import com.example.tlupickleball.adapters.Transaction_PersonalAdapter;
import com.example.tlupickleball.model.MemberFund1;
import com.example.tlupickleball.model.Transaction_Personal;
import com.example.tlupickleball.network.core.ApiClient;
import com.example.tlupickleball.network.core.SessionManager;
import com.example.tlupickleball.network.service.FinanceService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Personal_Finance_Fragment extends Fragment {

    private TextView tvName, tvFund, tvDonate, txTotalPenalty, txTotalPenaltyUnpaid, txTotalPenaltyPaid;
    private View rootView;
    private LinearLayout btnClick;
    private Spinner spinnerMonth;
    List<String> monthList = new ArrayList<>();
    int year = Calendar.getInstance().get(Calendar.YEAR);
    private FinanceService financeService;
    private RecyclerView rvTransactions_Personal;
    private Transaction_PersonalAdapter transactionPersonalAdapter;
    private List<Transaction_Personal> transactionPersonalList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_personal_finance, container, false);
        financeService = ApiClient.getClient(requireContext()).create(FinanceService.class);
        initViews();
        setupListeners();
        setupMonthSpinner();
        setupRecyclerView();
        loadFinanceStatus(SessionManager.getUid(requireContext()), Calendar.getInstance().get(Calendar.MONTH) + 1, year);
        return rootView;
    }

    private void initViews() {
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

        transactionPersonalList = new ArrayList<>();

        // Tạo dữ liệu đúng với constructor Transaction(title, amount, time, status, isIncome)
        transactionPersonalList.add(new Transaction_Personal("Đóng quỹ", "+100.000đ", "10/04/2025",  true));
        transactionPersonalList.add(new Transaction_Personal("Đóng quỹ", "+100.000đ", "10/05/2025",  true));
        transactionPersonalList.add(new Transaction_Personal("Đã đóng tiền phạt", "+100.000đ", "11/06/2025",  true));
        transactionPersonalList.add(new Transaction_Personal("Đóng quỹ", "+100.000đ", "10/06/2025",  true));
        transactionPersonalList.add(new Transaction_Personal("Đóng quỹ", "+100.000đ", "10/03/2025",  true));
        transactionPersonalList.add(new Transaction_Personal("Đã đóng tiền phạt", "+100.000đ", "11/05/2025",  true));

        transactionPersonalAdapter = new Transaction_PersonalAdapter(requireContext(),transactionPersonalList);
        rvTransactions_Personal.setAdapter(transactionPersonalAdapter);
    }

    private void filterTransactionsByMonth(int month) {
        List<Transaction_Personal> filteredList = new ArrayList<>();

        for (Transaction_Personal transactionPersonal : transactionPersonalList) {
            // Giả sử ngày có định dạng dd/MM/yyyy
            String[] parts = transactionPersonal.getTime().split("/");
            int transactionMonth = Integer.parseInt(parts[1]);

            if (transactionMonth == month) {
                filteredList.add(transactionPersonal);
            }
        }

        transactionPersonalAdapter.setData(filteredList); // Cập nhật danh sách đã lọc
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
                    tvFund.setText("Quỹ: " + data.getFixedFund().getAmount() + "đ");

                    // Ủng hộ: luôn màu xanh
                    tvDonate.setBackgroundResource(R.drawable.button_light_green_bg);
                    tvDonate.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_Dong));
                    tvDonate.setText("Ủng hộ: " + data.getTotalDonation() + "đ");

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

                        txTotalPenaltyUnpaid.setBackgroundResource(R.drawable.button_light_green_bg);
                        txTotalPenaltyUnpaid.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_Dong));
                    }

                    txTotalPenalty.setText("Tổng: " + total + "đ");
                    txTotalPenaltyUnpaid.setText("Còn thiếu: " + unpaid + "đ");
                    txTotalPenaltyPaid.setText("Đã đóng: " + paid + "đ");

                    Toast.makeText(requireContext(), "Tải dữ liệu thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Không thể tải dữ liệu tài chính", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MemberFund1> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}