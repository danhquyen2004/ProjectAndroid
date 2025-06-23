package com.example.tlupickleball.activities;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.tlupickleball.R;
import com.example.tlupickleball.activities.base.BaseMember;
import com.example.tlupickleball.adapters.MemberListAdapter;
import com.example.tlupickleball.model.User;
import com.example.tlupickleball.network.api_model.user.UserListResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class MemberList extends BaseMember {
    private RecyclerView recyclerView;
    private MemberListAdapter adapter;
    private List<User> lstUser;
    ImageButton btnBack;
    private static final float ACTION_BUTTON_WIDTH = 200;
    private final float buttonTotalWidth = ACTION_BUTTON_WIDTH * 2;
    private static final int REQUEST_CODE_MEMBER_DETAIL = 1001;
    private SwipeRefreshLayout swipeRefreshLayout;

    private boolean isSwipeEnabled = false; // Biến để kiểm soát việc vuốt

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_member_list);
        lstUser = new ArrayList<>();

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> onBackPressed());
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        fetchMembers();
        setupRecyclerView();
        swipeRefreshLayout.setOnRefreshListener(this::fetchMembers);

//        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
//                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//            private float currentSwipeDistance = 0;
//
//            @Override
//            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
//                                    @NonNull RecyclerView.ViewHolder viewHolder,
//                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
//                View itemView = viewHolder.itemView;
//
//                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
//                    float currentTranslation = itemView.getTranslationX();
//
//                    if (dX < 0) { // Vuốt trái
//                        float swipeDistance = Math.max(dX, -buttonTotalWidth);
//
//                        // Vẽ nút Từ chối (đỏ)
//                        Paint rejectPaint = new Paint();
//                        rejectPaint.setColor(Color.RED);
//                        RectF rejectButton = new RectF(
//                                itemView.getRight() + swipeDistance,
//                                itemView.getTop(),
//                                itemView.getRight() - ACTION_BUTTON_WIDTH,
//                                itemView.getBottom()
//                        );
//                        c.drawRect(rejectButton, rejectPaint);
//
//                        // Vẽ nút Quay lại (xám)
//                        Paint backPaint = new Paint();
//                        backPaint.setColor(Color.GRAY);
//                        RectF backButton = new RectF(
//                                itemView.getRight() - ACTION_BUTTON_WIDTH,
//                                itemView.getTop(),
//                                itemView.getRight(),
//                                itemView.getBottom()
//                        );
//                        c.drawRect(backButton, backPaint);
//
//                        drawText(c, "Từ chối", rejectButton, Color.WHITE);
//                        drawText(c, "Quay lại", backButton, Color.WHITE);
//
//                        itemView.setTranslationX(swipeDistance);
//                        currentSwipeDistance = swipeDistance;
//
//                    } else if (dX > 0) { // Vuốt phải
//                        if (currentTranslation < 0) { // Chỉ xử lý khi đã vuốt trái trước đó
//                            // Tính toán vị trí mới, giữ trong khoảng từ -buttonTotalWidth đến 0
//                            float newTranslation = Math.min(0, currentTranslation + dX);
//                            itemView.setTranslationX(newTranslation);
//                            currentSwipeDistance = newTranslation;
//                        } else {
//                            // Nếu chưa vuốt trái, không cho vuốt phải
//                            itemView.setTranslationX(0);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//                if (direction == ItemTouchHelper.LEFT) {
//                    // Khi vuốt trái hoàn tất, hiện buttons
//                    viewHolder.itemView.setTranslationX(-buttonTotalWidth);
//                } else if (direction == ItemTouchHelper.RIGHT) {
//                    // Khi vuốt phải hoàn tất, reset về vị trí ban đầu
//                    viewHolder.itemView.setTranslationX(0);
//                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
//                }
//            }
//        };
//
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
//        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Thêm touch listener để xử lý click vào button
//        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
//            @Override
//            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
//                View child = rv.findChildViewUnder(e.getX(), e.getY());
//                if (child != null && e.getAction() == MotionEvent.ACTION_UP) {
//                    float rawTouchX = e.getRawX();
//                    int[] itemLocation = new int[2];
//                    child.getLocationOnScreen(itemLocation);
//                    float itemRight = itemLocation[0] + child.getWidth();
//                    float translationX = child.getTranslationX();
//
//                    // Cộng thêm translationX để tính đúng vùng bấm khi vuốt
//                    float adjustedItemRight = itemRight + translationX;
//                    float distanceFromRight = adjustedItemRight - rawTouchX;
//
//                    if (distanceFromRight >= 0 && distanceFromRight <= ACTION_BUTTON_WIDTH * 2) {
//                        int position = rv.getChildAdapterPosition(child);
//                        if (position != RecyclerView.NO_POSITION) {
//                            if (distanceFromRight <= ACTION_BUTTON_WIDTH) {
//                                // Bấm nút Quay lại
//                                child.setTranslationX(0);
//                                adapter.notifyItemChanged(position);
//                                return true;
//                            } else {
//                                // Bấm nút Từ chối
//                                tuChoiThanhVien(position);
//                                return true;
//                            }
//                        }
//                    }
//                }
//
//
//
//                return false;
//            }
//
//            @Override
//            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {}
//
//            @Override
//            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
//        });
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MEMBER_DETAIL && resultCode == RESULT_OK) {
            fetchMembers(); // Chỉ load lại khi có RESULT_OK
        }
    }

    private void fetchMembers() {
        if (!swipeRefreshLayout.isRefreshing()) {
            showLoading();
        }
        userService.getAllUsers().enqueue(new retrofit2.Callback<UserListResponse>() {
            @Override
            public void onResponse(Call<UserListResponse> call, retrofit2.Response<UserListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = response.body().getUsers();
                    lstUser.clear();
                    lstUser.addAll(users);
                    adapter.notifyDataSetChanged();
                    hideLoading();
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    Toast.makeText(MemberList.this, "Failed to load members", Toast.LENGTH_SHORT).show();
                    hideLoading();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
            @Override
            public void onFailure(Call<UserListResponse> call, Throwable t) {
                Toast.makeText(MemberList.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                hideLoading();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.playerList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MemberListAdapter(this, lstUser, user -> {
            Intent intent = new Intent(this, MemberControllerInfor.class);
            intent.putExtra("uid", user.getUid());
            startActivityForResult(intent, REQUEST_CODE_MEMBER_DETAIL);
        });
        recyclerView.setAdapter(adapter);
    }

    private void tuChoiThanhVien(int position) {
        if (position >= 0 && position < lstUser.size()) {
            lstUser.remove(position);
            adapter.notifyItemRemoved(position);
            Toast.makeText(this, "Từ chối thành viên: " + position, Toast.LENGTH_SHORT).show();
        }
    }

    private void pheDuyetThanhVien(int position) {
        // TODO: Xử lý logic phê duyệt
        Toast.makeText(this, "Phê duyệt thành viên: " + position, Toast.LENGTH_SHORT).show();

        // Ví dụ: Cập nhật trạng thái
        lstUser.remove(position);
        adapter.notifyItemRemoved(position);
    }

}