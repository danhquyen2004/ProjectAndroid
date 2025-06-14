package com.example.tlupickleball.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
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
import com.example.tlupickleball.adapters.UserAdapter;
import com.example.tlupickleball.fragments.Home_Fragment;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

public class UserActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    public DrawerLayout drawerLayout; // public để fragment truy cập
    public NavigationView navigationView;
    public Toolbar toolbar;

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.layout_tab_bottom);

        drawerLayout = findViewById(R.id.main);
//        navigationView = findViewById(R.id.navigation_view);
//        toolbar = findViewById(R.id.toolbar);
//
//        // Set Toolbar nếu cần dùng
//        setSupportActionBar(toolbar);
//
//        // Hiển thị fragment ban đầu
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragment_container, new Home_Fragment())
//                .commit();

        tabLayout = findViewById(R.id.tabLayout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);

            // Set margin cho TabLayout
            View tabLayoutContainer = findViewById(R.id.tabLayoutContainer);
            if (tabLayoutContainer != null) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) tabLayout.getLayoutParams();
                params.bottomMargin = systemBars.bottom;
                tabLayout.setLayoutParams(params);
            }
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

    public void openDrawerFromRight() {
        drawerLayout.openDrawer(GravityCompat.END);
    }
}