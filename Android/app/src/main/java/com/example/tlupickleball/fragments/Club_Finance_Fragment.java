package com.example.tlupickleball.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.tlupickleball.R;
import com.example.tlupickleball.adapters.TransactionAdapter;
import com.example.tlupickleball.model.Transaction;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;


public class Club_Finance_Fragment extends Fragment {

    private View rootView;
    private RecyclerView rvTransactions;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        rootView = inflater.inflate(R.layout.fragment_club_finance, container, false);
        setupFilterClickListener();
        setupRecyclerView();
        return rootView;
    }


    private void setupFilterClickListener() {
        ImageView ivFilter = rootView.findViewById(R.id.ivFilter);
        ivFilter.setOnClickListener(v -> showFilterBottomSheet());
    }

    private void showFilterBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View sheetView = getLayoutInflater().inflate(R.layout.finance_filter, null);
        bottomSheetDialog.setContentView(sheetView);

        // Đóng bottom sheet khi bấm nút đóng
        ImageView ivClose = sheetView.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(v -> bottomSheetDialog.dismiss());

        // Xử lý bấm nút xác nhận
        Button btnConfirm = sheetView.findViewById(R.id.btnConfirmFilter);
        btnConfirm.setOnClickListener(v -> {
            // TODO: Lọc dữ liệu ở đây nếu muốn
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void setupRecyclerView() {
        rvTransactions = rootView.findViewById(R.id.rvTransactions);
        rvTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));

        transactionList = new ArrayList<>();

        // Tạo dữ liệu đúng với constructor Transaction(title, amount, time, status, isIncome)
        transactionList.add(new Transaction("Đóng quỹ", "+100.000đ", "10/05/2025",  true));
        transactionList.add(new Transaction("Đóng sân hàng tháng", "-2.100.000đ", "10/05/2025",  true));
        transactionList.add(new Transaction("Mua bóng", "-350.000đ", "11/05/2025",  false));

        transactionAdapter = new TransactionAdapter(transactionList);
        rvTransactions.setAdapter(transactionAdapter);
    }

}