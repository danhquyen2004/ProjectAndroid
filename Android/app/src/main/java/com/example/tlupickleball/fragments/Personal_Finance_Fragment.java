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

import com.example.tlupickleball.R;
import com.example.tlupickleball.activities.Activity_payment;
import com.example.tlupickleball.adapters.Transaction_PersonalAdapter;
import com.example.tlupickleball.model.Transaction_Personal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class Personal_Finance_Fragment extends Fragment {

    private View rootView;
    private LinearLayout btnClick;

    private Spinner spinnerMonth;
    List<String> monthList = new ArrayList<>();
    int year = Calendar.getInstance().get(Calendar.YEAR);

    private RecyclerView rvTransactions_Personal;
    private Transaction_PersonalAdapter transactionPersonalAdapter;
    private List<Transaction_Personal> transactionPersonalList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_personal_finance, container, false);

        setupMonthSpinner();
        setupRecyclerView();
        initViews();
        return rootView;
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

        transactionPersonalAdapter = new Transaction_PersonalAdapter(transactionPersonalList);
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

    private void initViews() {
        btnClick = rootView.findViewById(R.id.btn_click_pay);

        btnClick.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), Activity_payment.class);
            startActivity(intent);
            });
    }




//    private void initViews() {
//        btnDonate = rootView.findViewById(R.id.btn_donate);
//        btnFund = rootView.findViewById(R.id.btn_contribute_fund);
//        btnFine = rootView.findViewById(R.id.btn_fine);
//
//        View.OnClickListener qrClickListener = v -> showQrPopup();
//
//        btnDonate.setOnClickListener(qrClickListener);
//        btnFund.setOnClickListener(qrClickListener);
//        btnFine.setOnClickListener(qrClickListener);
//    }

}