package com.example.tlupickleball.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlupickleball.R;
import com.example.tlupickleball.adapters.NotificationAdapter;
import com.example.tlupickleball.model.NotificationItem;

import java.util.ArrayList;
import java.util.List;

public class notification extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<NotificationItem> notificationList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);

        recyclerView = findViewById(R.id.recyclerViewNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        notificationList = new ArrayList<>();
        notificationList.add(new NotificationItem("Chúc mừng thăng hạng", "Chúc mừng bạn đã giành vị trí top 1 sau trận vừa qua", "1 ngày trước"));
        notificationList.add(new NotificationItem("Đóng quỹ", "Sắp hết hạn đóng quỹ rồi. Bạn hãy nhớ đóng quỹ nhé", "2 ngày trước"));
        notificationList.add(new NotificationItem("Trận đấu sắp tới", "9:00, ngày 9 tháng 5. Bạn có 1 trận đấu", "3 ngày trước"));

        adapter = new NotificationAdapter(notificationList);
        recyclerView.setAdapter(adapter);
    }
}