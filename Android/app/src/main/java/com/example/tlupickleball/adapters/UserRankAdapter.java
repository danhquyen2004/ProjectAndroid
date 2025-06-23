package com.example.tlupickleball.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.tlupickleball.R;
import com.example.tlupickleball.model.Player;
import com.example.tlupickleball.model.User;
import com.example.tlupickleball.model.UserRank;

import java.util.List;

public class UserRankAdapter extends RecyclerView.Adapter<UserRankAdapter.PlayerViewHolder> {
    private List<UserRank> users;
    private Context context;

    public UserRankAdapter(Context context, List<UserRank> users)
    {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_player_rank, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        UserRank user = users.get(position);
        holder.txtName.setText(user.getFullName());
        holder.txtScore.setText(String.valueOf(user.getPoint()));
        Glide.with(context)
                .load(user.getAvatarUrl())
                .placeholder(R.drawable.default_avatar)
                .diskCacheStrategy(DiskCacheStrategy.NONE) // Bỏ qua cache trên đĩa
                .skipMemoryCache(true) // Bỏ qua cache trong bộ nhớ
                .circleCrop()
                .into(holder.imgAvatar);
        holder.txtRank.setText((String.valueOf(position + 4)));
    }

    public void setUsers(List<UserRank> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class PlayerViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView txtName, txtScore, txtRank;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtName = itemView.findViewById(R.id.txtName);
            txtScore = itemView.findViewById(R.id.txtScore);
            txtRank = itemView.findViewById(R.id.txtRank);
        }
    }
}