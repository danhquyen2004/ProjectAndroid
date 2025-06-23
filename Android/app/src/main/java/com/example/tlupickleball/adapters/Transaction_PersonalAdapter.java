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
import com.example.tlupickleball.model.logs;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class Transaction_PersonalAdapter extends RecyclerView.Adapter<Transaction_PersonalAdapter.TransactionViewHolder> {

    private final Context context;
    private List<logs> logList;

    public Transaction_PersonalAdapter(Context context, List<logs> logList) {
        this.context = context;
        this.logList = logList;
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAmount, tvTime;

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
        logs log = logList.get(position);
        holder.tvTitle.setText(log.getDescription());
        holder.tvAmount.setText(formatCurrency(log.getAmount()));
        holder.tvTime.setText(log.getCreatedAt());

        // Set text color based on the type of transaction
        holder.tvAmount.setTextColor(Color.parseColor("#000000"));

    }

    @Override
    public int getItemCount() {
        return logList != null ? logList.size() : 0;
    }

    public void setData(List<logs> logList) {
        this.logList = logList;
        notifyDataSetChanged();
    }

    private String formatCurrency(long amount) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        return nf.format(amount) + "đ";
    }
}

