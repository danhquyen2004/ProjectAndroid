package com.example.tlupickleball.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlupickleball.R;
import com.example.tlupickleball.model.Transaction_Personal;

import java.util.List;

public class Transaction_PersonalAdapter extends RecyclerView.Adapter<Transaction_PersonalAdapter.TransactionViewHolder> {

    private final Context context;
    private List<Transaction_Personal> transactionPersonals;

    public Transaction_PersonalAdapter(Context context, List<Transaction_Personal> transactionPersonals) {
        this.context = context;
        this.transactionPersonals = transactionPersonals;
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAmount, tvTime, tvStatus;

        public TransactionViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction_person, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction_Personal transactionPersonal = transactionPersonals.get(position);
        holder.tvTitle.setText(transactionPersonal.getTitle());
        holder.tvAmount.setText(transactionPersonal.getAmount());
        holder.tvTime.setText(transactionPersonal.getTime());

        // Màu tiền
        if (transactionPersonal.isIncome()) {
            holder.tvAmount.setTextColor(Color.parseColor("#009900")); // xanh
        } else {
            holder.tvAmount.setTextColor(Color.parseColor("#FF0000")); // đỏ
        }
    }

    @Override
    public int getItemCount() {
        return transactionPersonals.size();
    }

    public void setData(List<Transaction_Personal> newList) {
        this.transactionPersonals = newList;
        notifyDataSetChanged();
    }



}

