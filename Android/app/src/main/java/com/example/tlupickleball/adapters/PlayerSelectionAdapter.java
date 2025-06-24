package com.example.tlupickleball.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tlupickleball.R; // Đảm bảo R.drawable.default_avatar tồn tại
import com.example.tlupickleball.model.User;

import java.util.ArrayList;
import java.util.List;

public class PlayerSelectionAdapter extends RecyclerView.Adapter<PlayerSelectionAdapter.PlayerViewHolder> {

    private List<User> playersDisplayed; // Danh sách đang hiển thị
    private List<User> allOriginalPlayers; // Danh sách gốc ban đầu (từ API)
    private OnPlayerSelectedListener listener;

    public interface OnPlayerSelectedListener {
        void onPlayerSelected(User player);
    }

    public PlayerSelectionAdapter(List<User> initialPlayers, OnPlayerSelectedListener listener) {
        this.allOriginalPlayers = new ArrayList<>(initialPlayers); // Lưu bản sao của danh sách ban đầu
        this.playersDisplayed = new ArrayList<>(initialPlayers); // Bắt đầu với danh sách ban đầu
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player_search, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        User player = playersDisplayed.get(position);
        holder.playerName.setText(player.getFullName()); // Hiển thị tên đầy đủ trong danh sách tìm kiếm

        if (player.getAvatarUrl() != null && !player.getAvatarUrl().isEmpty()) {
            Glide.with(holder.playerAvatar.getContext())
                    .load(player.getAvatarUrl())
                    .into(holder.playerAvatar);
        } else {}

        // Thiết lập OnClickListener cho mỗi item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlayerSelected(player);
            }
        });
    }

    @Override
    public int getItemCount() {
        return playersDisplayed != null ? playersDisplayed.size() : 0;
    }

    // Phương thức để cập nhật danh sách (khi tải từ API hoặc reset lọc)
    public void updateList(List<User> newList) {
        this.allOriginalPlayers.clear(); // Xóa và thêm toàn bộ danh sách mới vào bản gốc
        this.allOriginalPlayers.addAll(newList);

        this.playersDisplayed.clear(); // Xóa và thêm toàn bộ danh sách mới vào danh sách hiển thị
        this.playersDisplayed.addAll(newList);
        notifyDataSetChanged();
    }

    // Phương thức lọc danh sách
    public void filter(String query) {
        playersDisplayed.clear();
        if (query.isEmpty()) {
            playersDisplayed.addAll(allOriginalPlayers); // Nếu rỗng, hiển thị lại tất cả
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (User user : allOriginalPlayers) {
                // Kiểm tra null cho getFullName() trước khi gọi toLowerCase()
                if (user.getFullName() != null && user.getFullName().toLowerCase().contains(lowerCaseQuery)) {
                    playersDisplayed.add(user);
                }
            }
        }
        notifyDataSetChanged(); // Thông báo cho RecyclerView rằng dữ liệu đã thay đổi
    }

    static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView playerName;
        ImageView playerAvatar;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            playerName = itemView.findViewById(R.id.player_name);
            playerAvatar = itemView.findViewById(R.id.player_avatar);
        }
    }
}