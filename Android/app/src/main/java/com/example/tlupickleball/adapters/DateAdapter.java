package com.example.tlupickleball.adapters;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tlupickleball.R;
import com.example.tlupickleball.model.DateItem;

import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {

    public interface OnDateSelectedListener {
        void onDateSelected(DateItem date);
    }

    private List<DateItem> dates;
    private OnDateSelectedListener onDateSelectedListener;
    private int selectedPosition = RecyclerView.NO_POSITION; // Vị trí của mục đang được chọn
    private int recyclerViewWidth = 0; // Thêm biến để lưu chiều rộng của RecyclerView

    // Constructor mới có thêm tham số recyclerViewWidth
    public DateAdapter(List<DateItem> dates, OnDateSelectedListener onDateSelectedListener, int recyclerViewWidth) {
        this.dates = dates;
        this.onDateSelectedListener = onDateSelectedListener;
        this.recyclerViewWidth = recyclerViewWidth;
        // Khởi tạo vị trí chọn ban đầu nếu có, dựa trên trạng thái isSelected của DateItem
        for (int i = 0; i < dates.size(); i++) {
            if (dates.get(i).isSelected()) {
                selectedPosition = i;
                break;
            }
        }
        Log.d("DateAdapter", "Initial selectedPosition: " + selectedPosition);
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_date, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        DateItem date = dates.get(position);
        holder.txtDate.setText(date.getDayOfMonth() + "\n" + date.getMonth());

        // --- Logic dàn đều các item ngày tháng ---
        if (recyclerViewWidth > 0 && getItemCount() > 0) {
            // Giả sử bạn muốn hiển thị 5 ngày cùng lúc trên màn hình
            int itemsToShow = 5; // Số lượng ngày muốn hiển thị trên màn hình
            int itemWidth = recyclerViewWidth / itemsToShow;

            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            layoutParams.width = itemWidth;
            holder.itemView.setLayoutParams(layoutParams);
        }
        // --- Kết thúc logic dàn đều ---

        // Cập nhật giao diện của mục dựa trên trạng thái chọn
        if (position == selectedPosition) {
            holder.txtDate.setBackgroundResource(R.drawable.bg_date_selected);
            holder.txtDate.setTextColor(Color.WHITE);
            // Đảm bảo rằng DateItem cũng được đánh dấu là selected
            if (!date.isSelected()) {
                date.setSelected(true);
            }
        } else {
            holder.txtDate.setBackgroundResource(android.R.color.transparent);
            holder.txtDate.setTextColor(Color.BLACK);
            // Đảm bảo rằng DateItem cũng được đánh dấu là không selected
            if (date.isSelected()) {
                date.setSelected(false);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickedPosition = holder.getAdapterPosition();
                if (clickedPosition != RecyclerView.NO_POSITION) {

                    // Nếu click vào ngày đang được chọn, không làm gì cả
                    if (clickedPosition == selectedPosition) {
                        Log.d("DateAdapter", "Clicked on already selected item at position: " + clickedPosition);
                        return;
                    }

                    // Bỏ chọn mục cũ (nếu có mục nào đang được chọn)
                    if (selectedPosition != RecyclerView.NO_POSITION) {
                        dates.get(selectedPosition).setSelected(false);
                        notifyItemChanged(selectedPosition); // Cập nhật UI cho mục cũ
                    }

                    // Cập nhật vị trí được chọn mới
                    selectedPosition = clickedPosition;
                    dates.get(selectedPosition).setSelected(true); // Đánh dấu mục hiện tại là được chọn
                    notifyItemChanged(selectedPosition); // Cập nhật UI cho mục mới

                    // Thông báo cho Listener rằng một ngày đã được chọn
                    onDateSelectedListener.onDateSelected(dates.get(selectedPosition));
                    Log.d("DateAdapter", "New selected position: " + selectedPosition);
                } else {
                    Log.w("DateAdapter", "Clicked position is NO_POSITION.");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txtDate);
        }
    }
}