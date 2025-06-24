package com.example.tlupickleball.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tlupickleball.R;
import com.example.tlupickleball.model.NotificationItem;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<NotificationItem> notificationList;

    public NotificationAdapter(List<NotificationItem> notificationList) {
        this.notificationList = notificationList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, content, time;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txt_noti_title);
            content = itemView.findViewById(R.id.txt_noti_content);
            time = itemView.findViewById(R.id.txt_noti_time);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NotificationItem item = notificationList.get(position);
        holder.title.setText(item.getTitle());
        holder.content.setText(item.getContent());
        holder.time.setText(item.getTime());
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }
}

