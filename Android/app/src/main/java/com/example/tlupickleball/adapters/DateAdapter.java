package com.example.tlupickleball.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlupickleball.R;
import com.example.tlupickleball.model.DateItem;

import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {

    private Context context;
    private List<DateItem> dateList;
    private OnDateClickListener onDateClickListener;
    private int selectedPosition = -1; // Ban đầu không có item nào được chọn

    public interface OnDateClickListener {
        void onDateClick(DateItem dateModel, int position);
    }

    public DateAdapter(Context context, List<DateItem> dateList, OnDateClickListener listener) {
        this.context = context;
        this.dateList = dateList;
        this.onDateClickListener = listener;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_date, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        DateItem dateModel = dateList.get(position);
        holder.txtDate.setText(dateModel.getFormattedDate());

        if (selectedPosition == position) {
            holder.txtDate.setBackgroundResource(R.drawable.bg_date_selected);
            holder.txtDate.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        } else {
            holder.txtDate.setBackgroundResource(R.drawable.bg_unselected_date);
            // Giả sử màu text mặc định trong item_date.xml là màu tối
            holder.txtDate.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        }

        holder.itemView.setOnClickListener(v -> {
            if (onDateClickListener != null) {
                int previousSelectedPosition = selectedPosition;
                selectedPosition = holder.getAdapterPosition();

                // Cập nhật lại item đã chọn trước đó (nếu có)
                if (previousSelectedPosition != -1) {
                    notifyItemChanged(previousSelectedPosition);
                }
                // Cập nhật item mới được chọn
                notifyItemChanged(selectedPosition);

                onDateClickListener.onDateClick(dateModel, selectedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dateList == null ? 0 : dateList.size();
    }

    public void setSelectedPosition(int position) {
        int previousSelectedPosition = selectedPosition;
        selectedPosition = position;
        if (previousSelectedPosition != -1) {
            notifyItemChanged(previousSelectedPosition);
        }
        if (selectedPosition != -1) {
            notifyItemChanged(selectedPosition);
        }
    }


    static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txtDate);
        }
    }
}