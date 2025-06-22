package com.example.tlupickleball.fragments;

import android.content.Intent;
import android.os.Bundle;

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
        loadFinanceStatus(SessionManager.getUid(requireContext()));
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
                String selectedMonth = parent.getItemAtPosition(position).toString();
                // Xử lý logic theo tháng được chọn

                // Tách tháng từ chuỗi "Tháng x/yyyy"
                int selectedMonthNumber = Integer.parseInt(selectedMonth.split(" ")[1].split("/")[0]);
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

    private void loadFinanceStatus(String userId) {

        financeService.financeStatus(userId).enqueue(new Callback<MemberFund1>() {
            @Override
            public void onResponse(Call<MemberFund1> call, Response<MemberFund1> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MemberFund1 data = response.body();
                    tvFund.setText(String.valueOf(data.getFixedFund().getAmount()));
                    tvDonate.setText(String.valueOf(data.getTotalDonation()));
                    txTotalPenalty.setText(String.valueOf(data.getTotalPenalty()));
                    txTotalPenaltyUnpaid.setText(String.valueOf(data.getTotalPenaltyUnpaid()));
                    txTotalPenaltyPaid.setText(String.valueOf(data.getTotalPenaltyPaid()));
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