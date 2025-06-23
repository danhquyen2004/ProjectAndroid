package com.example.tlupickleball.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tlupickleball.R;
import com.example.tlupickleball.activities.base.BaseActivity;
import com.example.tlupickleball.adapters.UserAdapter;
import com.example.tlupickleball.network.core.SessionManager;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

public class UserActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {

    public DrawerLayout drawerLayout; // public để fragment truy cập
    public NavigationView navigationView;
    public Toolbar toolbar;

    private ImageButton closeBtn; // Nút đóng drawer, nếu cần
    private LinearLayout logoutLayout, userProfileLayout;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private boolean isDialogShowing;
    Dialog dialogForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.layout_tab_bottom);

        drawerLayout = findViewById(R.id.main);
        closeBtn = findViewById(R.id.close_drawer_button);
        logoutLayout = findViewById(R.id.log_out_btn);
        userProfileLayout = findViewById(R.id.user_profile_btn);

        closeBtn.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.END));

        logoutLayout.setOnClickListener(v -> {
            // Xử lý logout tại đây
            showDialogForm();
        });

        userProfileLayout.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, MemberInfor.class);
//            intent.putExtra("name", player.getName());
//            intent.putExtra("email", player.getEmail());
//            intent.putExtra("avatar", player.getAvatarResourceId());
            context.startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.END);
        });

        tabLayout = findViewById(R.id.tabLayout);

        // Thiết lập cũ
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
//
//            // Set margin cho TabLayout
//            View tabLayoutContainer = findViewById(R.id.tabLayoutContainer);
//            if (tabLayoutContainer != null) {
//                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) tabLayout.getLayoutParams();
//                params.bottomMargin = systemBars.bottom;
//                tabLayout.setLayoutParams(params);
//            }
//            return insets;
//        });

        // Thiết lập mới với TabLayoutContainer
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tabLayoutContainer), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, 0, 0, systemBars.bottom); // Trừ phần điều hướng Android
            return insets;
        });


        tabLayout.setTabMode(TabLayout.MODE_FIXED); // Chế độ fixed width
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL); // Fill full width

        viewPager2 = findViewById(R.id.viewPager2);
        FragmentManager manager = getSupportFragmentManager();
        UserAdapter userPagerAdapter = new UserAdapter(manager, getLifecycle());
        viewPager2.setAdapter(userPagerAdapter);

        tabLayout.addTab(tabLayout.newTab().setText("Trang chủ")
                .setIcon(R.drawable.ic_home));
        tabLayout.addTab(tabLayout.newTab().setText("Trận đấu")
                .setIcon(R.drawable.ic_match));
        tabLayout.addTab(tabLayout.newTab().setText("Tài chính")
                .setIcon(R.drawable.ic_finance));
        tabLayout.addTab(tabLayout.newTab().setText("Xếp hạng")
                .setIcon(R.drawable.ic_rank));
        tabLayout.addTab(tabLayout.newTab().setText("Hội viên")
                .setIcon(R.drawable.ic_membercontroller));

        tabLayout.addOnTabSelectedListener(this);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager2.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private void showDialogForm() {
        if (isDialogShowing) {
            return; // Nếu dialog đã hiển thị, không làm gì cả
        }

        dialogForm = new Dialog(UserActivity.this);
        dialogForm.setContentView(R.layout.view_dialog_form);
        dialogForm.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogForm.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_box));
        dialogForm.setCancelable(false);

        isDialogShowing = true;

        // Tìm các view trong dialog
        ImageView iconDialog = dialogForm.findViewById(R.id.icon_dialog);
        TextView titleDialog = dialogForm.findViewById(R.id.tv_confirm);
        TextView messageDialog = dialogForm.findViewById(R.id.tv_confirm_infor);
        Button btnDiaLogOK = dialogForm.findViewById(R.id.btn_approve);
        Button btnDiaLogCancel = dialogForm.findViewById(R.id.btn_reject);


        iconDialog.setImageResource(R.drawable.info_warning_fill);
        titleDialog.setText("Bạn có muốn đăng xuất");
        messageDialog.setText("Phiên đăng nhập của bạn sẽ kết thúc");
        btnDiaLogOK.setText("Đăng xuất");
        btnDiaLogCancel.setText("Quay lại");

        btnDiaLogOK.setOnClickListener(v -> {
            // Xử lý logic phê duyệt/từ chối
            LogOut();
            isDialogShowing = false; // Đánh dấu dialog đã đóng
            dialogForm.dismiss();
        });

        btnDiaLogCancel.setOnClickListener(v -> {
            isDialogShowing = false; // Reset trạng thái
            dialogForm.dismiss();
        });

        // Thêm listener khi dialog bị dismiss
        dialogForm.setOnDismissListener(dialog -> {
            isDialogShowing = false;
        });

        dialogForm.show();
    }

    private void LogOut() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_REMEMBER, false);
        editor.apply();
        SessionManager.clearSession(this);
        // Tạo intent để về màn hình login
        Intent intent = new Intent(UserActivity.this, LoginActivity.class);
        // Xóa toàn bộ activity stack
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}