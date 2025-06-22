package com.example.tlupickleball.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlupickleball.R;
import com.example.tlupickleball.model.User;
import com.squareup.picasso.Picasso; // Để tải ảnh (thêm dependency nếu chưa có)

import java.util.ArrayList;
import java.util.List;

public class PlayerSelectionAdapter extends RecyclerView.Adapter<PlayerSelectionAdapter.PlayerViewHolder> {

    private List<User> playerList;
    private List<User> filteredPlayerList;
    private OnPlayerSelectedListener onPlayerSelectedListener;

    public interface OnPlayerSelectedListener {
        void onPlayerSelected(User player);
    }

    public PlayerSelectionAdapter(List<User> playerList, OnPlayerSelectedListener listener) {
        this.playerList = playerList;
        this.filteredPlayerList = new ArrayList<>(playerList);
        this.onPlayerSelectedListener = listener;
    }

    public void updateList(List<User> newList) {
        this.playerList = newList;
        this.filteredPlayerList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    public void filter(String text) {
        filteredPlayerList.clear();
        if (text.isEmpty()) {
            filteredPlayerList.addAll(playerList);
        } else {
            text = text.toLowerCase();
            for (User item : playerList) {
                if (item.getFullName() != null && item.getFullName().toLowerCase().contains(text)) {
                    filteredPlayerList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player_search, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        User player = filteredPlayerList.get(position);
        holder.playerNameTextView.setText(player.getFullName());

        // Tải avatar nếu có
        if (player.getAvatarUrl() != null && !player.getAvatarUrl().isEmpty()) {
            Picasso.get().load(player.getAvatarUrl()).placeholder(R.drawable.circle_shape).into(holder.playerAvatarImageView);
        } else {
            holder.playerAvatarImageView.setImageResource(R.drawable.circle_shape); // Avatar mặc định
        }

        holder.itemView.setOnClickListener(v -> {
            if (onPlayerSelectedListener != null) {
                onPlayerSelectedListener.onPlayerSelected(player);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredPlayerList.size();
    }

    public static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView playerNameTextView;
        ImageView playerAvatarImageView;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            playerNameTextView = itemView.findViewById(R.id.player_name);
            playerAvatarImageView = itemView.findViewById(R.id.player_avatar);
        }
    }
}