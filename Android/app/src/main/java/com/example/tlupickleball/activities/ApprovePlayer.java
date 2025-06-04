package com.example.tlupickleball.activities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlupickleball.R;
import com.example.tlupickleball.adapters.ApprovePlayerAdapter;
import com.example.tlupickleball.adapters.PlayerAdapter;
import com.example.tlupickleball.fragments.Top_Rank_Fragment;
import com.example.tlupickleball.model.Player;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ApprovePlayer extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ApprovePlayerAdapter adapter;
    private List<Player> lstPlayer;
    private static final float ACTION_BUTTON_WIDTH = 200;
    private final float buttonTotalWidth = ACTION_BUTTON_WIDTH * 2;

    private boolean isSwipeEnabled = false; // Biến để kiểm soát việc vuốt

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_approve_player);

        initData();
        setupRecyclerView();

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            private float currentSwipeDistance = 0;

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    float currentTranslation = itemView.getTranslationX();

                    if (dX < 0) { // Vuốt trái
                        float swipeDistance = Math.max(dX, -buttonTotalWidth);

                        // Vẽ nút Từ chối (đỏ)
                        Paint rejectPaint = new Paint();
                        rejectPaint.setColor(Color.RED);
                        RectF rejectButton = new RectF(
                                itemView.getRight() + swipeDistance,
                                itemView.getTop(),
                                itemView.getRight() - ACTION_BUTTON_WIDTH,
                                itemView.getBottom()
                        );
                        c.drawRect(rejectButton, rejectPaint);

                        // Vẽ nút Quay lại (xám)
                        Paint backPaint = new Paint();
                        backPaint.setColor(Color.GRAY);
                        RectF backButton = new RectF(
                                itemView.getRight() - ACTION_BUTTON_WIDTH,
                                itemView.getTop(),
                                itemView.getRight(),
                                itemView.getBottom()
                        );
                        c.drawRect(backButton, backPaint);

                        drawText(c, "Từ chối", rejectButton, Color.WHITE);
                        drawText(c, "Quay lại", backButton, Color.WHITE);

                        itemView.setTranslationX(swipeDistance);
                        currentSwipeDistance = swipeDistance;

                    } else if (dX > 0) { // Vuốt phải
                        if (currentTranslation < 0) { // Chỉ xử lý khi đã vuốt trái trước đó
                            // Tính toán vị trí mới, giữ trong khoảng từ -buttonTotalWidth đến 0
                            float newTranslation = Math.min(0, currentTranslation + dX);
                            itemView.setTranslationX(newTranslation);
                            currentSwipeDistance = newTranslation;
                        } else {
                            // Nếu chưa vuốt trái, không cho vuốt phải
                            itemView.setTranslationX(0);
                        }
                    }
                }
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT) {
                    // Khi vuốt trái hoàn tất, hiện buttons
                    viewHolder.itemView.setTranslationX(-buttonTotalWidth);
                } else if (direction == ItemTouchHelper.RIGHT) {
                    // Khi vuốt phải hoàn tất, reset về vị trí ban đầu
                    viewHolder.itemView.setTranslationX(0);
                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Thêm touch listener để xử lý click vào button
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && e.getAction() == MotionEvent.ACTION_UP) {
                    float rawTouchX = e.getRawX();
                    int[] itemLocation = new int[2];
                    child.getLocationOnScreen(itemLocation);
                    float itemRight = itemLocation[0] + child.getWidth();
                    float translationX = child.getTranslationX();

                    // Cộng thêm translationX để tính đúng vùng bấm khi vuốt
                    float adjustedItemRight = itemRight + buttonTotalWidth;
                    float distanceFromRight = adjustedItemRight - rawTouchX;

                    if (distanceFromRight >= 0 && distanceFromRight <= ACTION_BUTTON_WIDTH * 2) {
                        int position = rv.getChildAdapterPosition(child);
                        if (position != RecyclerView.NO_POSITION) {
                            if (distanceFromRight <= ACTION_BUTTON_WIDTH) {
                                // Bấm nút Quay lại
                                child.setTranslationX(0);
                                adapter.notifyItemChanged(position);
                                return true;
                            } else {
                                // Bấm nút Từ chối
                                tuChoiThanhVien(position);
                                return true;
                            }
                        }
                    }
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {}

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void drawText(Canvas canvas, String text, RectF button, int color) {
        Paint textPaint = new Paint();
        textPaint.setColor(color);
        textPaint.setTextSize(40);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);

        float textX = button.centerX();
        float textY = button.centerY() - ((textPaint.descent() + textPaint.ascent()) / 2);
        canvas.drawText(text, textX, textY, textPaint);
    }

    private void initData() {
        lstPlayer = new ArrayList<>();
        lstPlayer.add(new Player("Ahri", "hihihaha@gmail.com", R.drawable.avatar_1));
        lstPlayer.add(new Player("Lux", "hihehh@gmail.com", R.drawable.avatar_1));
        lstPlayer.add(new Player("Jinx", "hiha@gmail.com", R.drawable.avatar_1));
        lstPlayer.add(new Player("Vi", "hihaha@gmail.com", R.drawable.avatar_1));
        lstPlayer.add(new Player("Zed", "hihoha@gmail.com", R.drawable.avatar_1));
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.playerList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ApprovePlayerAdapter(this, lstPlayer);
        recyclerView.setAdapter(adapter);
    }

    private void tuChoiThanhVien(int position) {
        if (position >= 0 && position < lstPlayer.size()) {
            lstPlayer.remove(position);
            adapter.notifyItemRemoved(position);
            Toast.makeText(this, "Từ chối thành viên: " + position, Toast.LENGTH_SHORT).show();
        }
    }

    private void pheDuyetThanhVien(int position) {
        // TODO: Xử lý logic phê duyệt
        Toast.makeText(this, "Phê duyệt thành viên: " + position, Toast.LENGTH_SHORT).show();

        // Ví dụ: Cập nhật trạng thái
        lstPlayer.remove(position);
        adapter.notifyItemRemoved(position);
    }

}