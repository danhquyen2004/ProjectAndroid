package com.example.tlupickleball.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlupickleball.R;
import com.example.tlupickleball.model.Transaction_Club;

import java.util.List;

public class Transaction_ClubAdapter extends RecyclerView.Adapter<Transaction_ClubAdapter.HistoryViewHolder> {

    private List<Transaction_Club> transactionClub;

    public Transaction_ClubAdapter(List<Transaction_Club> transactionList) {
        this.transactionClub = transactionList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction_club, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Transaction_Club item = transactionClub.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvDescription.setText(item.getDescription());
        holder.tvDate.setText(item.getDate());
        holder.tvAmount.setText(item.getAmount());

        // Đổi màu cho khoản thu/chi
        if (item.getAmount().startsWith("-")) {
            holder.tvAmount.setTextColor(Color.parseColor("#FF0000")); //đỏ
        } else {
            holder.tvAmount.setTextColor(Color.parseColor("#009900")); //xanh
        }
    }

    @Override
    public int getItemCount() {
        return transactionClub.size();
    }

    public void setData(List<Transaction_Club> newList) {
        this.transactionClub = newList;
        notifyDataSetChanged();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvDate, tvAmount;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }
    }
}
