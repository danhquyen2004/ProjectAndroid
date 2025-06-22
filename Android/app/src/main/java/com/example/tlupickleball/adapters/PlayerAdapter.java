package com.example.tlupickleball.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlupickleball.R;
import com.example.tlupickleball.model.Player;

import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {
    private List<Player> playerList;
    private Context context;

    public PlayerAdapter(Context context,List<Player> playerList)
    {
        this.context = context;
        this.playerList = playerList;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_player_rank, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Player player = playerList.get(position);
        holder.txtName.setText(player.getName());
        holder.txtScore.setText(String.valueOf(player.getSoloPoint()));
        holder.imgAvatar.setImageResource(player.getAvatarResourceId());
        holder.txtRank.setText((String.valueOf(position + 4)));
    }

    @Override
    public int getItemCount() {
        return playerList.size();
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