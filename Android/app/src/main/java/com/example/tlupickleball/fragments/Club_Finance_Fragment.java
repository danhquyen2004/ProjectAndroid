package com.example.tlupickleball.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tlupickleball.R;
import com.example.tlupickleball.adapters.Transaction_ClubAdapter;
import com.example.tlupickleball.adapters.Transaction_PersonalAdapter;
import com.example.tlupickleball.model.Transaction_Club;
import com.example.tlupickleball.model.Transaction_Personal;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class Club_Finance_Fragment extends Fragment {

    private View rootView;
    // Biến giao diện
    private Button btnAddExpense;
    private TextView txtFund, txtRevenue, txtExpenses;
    private LinearLayout llDonate;
    private Spinner spinnerMonth;
    private int selectedMonthNumber;
    List<String> monthList = new ArrayList<>();
    int year = Calendar.getInstance().get(Calendar.YEAR);
    private RecyclerView rvTransactions;
    private Transaction_ClubAdapter transactionClubAdapter;
    private List<Transaction_Club> transactionClubList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_club_finance, container, false);
        initViews();
        setupListeners();
        setupMonthSpinner();
        setupRecyclerView();
        return rootView;
    }


    private void initViews() {
        btnAddExpense = rootView.findViewById(R.id.btnAddExpense);
        txtFund = rootView.findViewById(R.id.tvFund);
        txtRevenue = rootView.findViewById(R.id.tvRevenue);
        txtExpenses = rootView.findViewById(R.id.tvExpenses);
        llDonate = rootView.findViewById(R.id.llDonate);
    }

    private void setupListeners() {
        btnAddExpense.setOnClickListener(v -> showAddExpensePopup());
    }


    private void setupRecyclerView() {
        rvTransactions = rootView.findViewById(R.id.recyclerFundClubHistory);
        rvTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));

        transactionClubList = new ArrayList<>();

        transactionClubList.add(new Transaction_Club("Đóng quỹ", "Nguyễn Giang Đông + đã đóng", "18:01 - 10/06/2025", "+100.000đ"));
        transactionClubList.add(new Transaction_Club("Đóng quỹ", "Nguyễn Giang Đông + đã đóng", "20:01 - 10/05/2025", "+100.000đ"));
        transactionClubList.add(new Transaction_Club("Đóng quỹ", "Nguyễn Văn A + đã đóng", "20:01 - 10/05/2025", "+100.000đ"));
        transactionClubList.add(new Transaction_Club( "Đóng sân hàng tháng", "", "20:01 - 10/05/2025", "-2.100.000đ"));
        transactionClubList.add(new Transaction_Club(  "Mua bóng", "", "20:01 - 10/05/2025", "-350.000đ"));
        transactionClubList.add(new Transaction_Club("Đóng quỹ", "Nguyễn Giang Đông + đã đóng", "12:00 - 10/04/2025", "+100.000đ"));
        transactionClubList.add(new Transaction_Club("Đóng quỹ", "Nguyễn Văn A + đã đóng", "21:12 - 09/04/2025", "+100.000đ"));
        transactionClubAdapter = new Transaction_ClubAdapter(transactionClubList);
        rvTransactions.setAdapter(transactionClubAdapter);
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
                selectedMonthNumber = Integer.parseInt(selectedMonth.split(" ")[1].split("/")[0]);
                filterTransactionsByMonth(selectedMonthNumber);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không chọn gì
            }
        });
    }

    private void filterTransactionsByMonth(int month) {
        List<Transaction_Club> filteredList = new ArrayList<>();

        for (Transaction_Club transactionClub : transactionClubList) {
            // Giả sử ngày có định dạng dd/MM/yyyy
            String[] parts = transactionClub.getDate().split("/");
            int transactionMonth = Integer.parseInt(parts[1]);
            if (transactionMonth == month) {
                filteredList.add(transactionClub);
            }
        }

        transactionClubAdapter.setData(filteredList); // Cập nhật danh sách đã lọc
    }

    // Hàm này sẽ được gọi khi người dùng nhấn nút "Thêm giao dịch"
    private void showAddExpensePopup() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_add_expense, null);
        dialog.setContentView(view);

        EditText edtDescription = view.findViewById(R.id.edtDescription);
        EditText edtAmount = view.findViewById(R.id.edtAmount);
        EditText edtDate = view.findViewById(R.id.edtDate);
        Button btnConfirm = view.findViewById(R.id.btnConfirm);

        // Mặc định chọn ngày hôm nay
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        edtDate.setText(sdf.format(calendar.getTime()));

        edtDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view1, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        edtDate.setText(sdf.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        btnConfirm.setOnClickListener(v -> {
            String desc = edtDescription.getText().toString().trim();
            String amount = edtAmount.getText().toString().trim();
            String date = edtDate.getText().toString();

            if (desc.isEmpty() || amount.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            String formattedAmount = "-" + amount + "đ"; // Giả sử là chi
            transactionClubList.add(0, new Transaction_Club(desc, "", "20:00 - " + date, formattedAmount));
            transactionClubAdapter.setData(transactionClubList);
            filterTransactionsByMonth(selectedMonthNumber);
            dialog.dismiss();
        });
        dialog.show();
    }


}