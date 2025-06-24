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
import com.example.tlupickleball.model.Transaction_Club;
import com.example.tlupickleball.model.logClub;
import com.example.tlupickleball.model.logs;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Transaction_ClubAdapter extends RecyclerView.Adapter<Transaction_ClubAdapter.HistoryViewHolder> {

    private final Context context;
    private List<logClub> logClubList = new ArrayList<>();

    public Transaction_ClubAdapter(Context context, List<logClub> logClubList) {
        this.context = context;
        if (logClubList != null) this.logClubList = logClubList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction_club, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        logClub log = logClubList.get(position);

        holder.tvTitle.setText(log.getReason());
        holder.tvDescription.setText(log.getReason());
        holder.tvDate.setText(formatDate(log.getCreatedAt()));
        holder.tvAmount.setText("-" + formatCurrency(log.getAmount()));

        // Always red for expense (if you have income, add logic here)
        holder.tvAmount.setTextColor(Color.parseColor("#FF0000"));
    }

    @Override
    public int getItemCount() {
        return logClubList != null ? logClubList.size() : 0;
    }

    public void setData(List<logClub> newList) {
        this.logClubList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    private String formatDate(String isoDate) {
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            SimpleDateFormat outFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return outFormat.format(isoFormat.parse(isoDate));
        } catch (ParseException e) {
            return "";
        }
    }

    private String formatCurrency(long amount) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        return nf.format(amount) + "đ";
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
