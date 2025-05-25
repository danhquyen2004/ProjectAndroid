package com.example.tlupickleball.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlupickleball.R;
import com.example.tlupickleball.model.DateItem;

import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.ViewHolder> {
    private final List<DateItem> dates;
    private final OnDateClickListener listener;
    private int selectedPosition = 0;

    public interface OnDateClickListener {
        void onDateClick(DateItem date);
    }

    public DateAdapter(List<DateItem> dates, OnDateClickListener listener) {
        this.dates = dates;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate;
        public ViewHolder(View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txtDate);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DateItem date = dates.get(position);
        holder.txtDate.setText(date.getDisplayText());
        holder.txtDate.setBackgroundResource(position == selectedPosition ? R.drawable.bg_date_selected : R.drawable.bg_unselected_date);
        holder.itemView.setOnClickListener(v -> {
            selectedPosition = position;
            listener.onDateClick(date);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }
}
