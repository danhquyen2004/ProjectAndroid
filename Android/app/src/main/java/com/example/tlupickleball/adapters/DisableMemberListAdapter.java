package com.example.tlupickleball.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlupickleball.R;
import com.example.tlupickleball.activities.DisableMemberInfor;
import com.example.tlupickleball.model.Player;

import java.util.List;

public class DisableMemberListAdapter extends RecyclerView.Adapter<DisableMemberListAdapter.ApprovePlayerViewHolder> {
    private List<Player> playerList;
    private Context context;

    public DisableMemberListAdapter(Context context, List<Player> playerList)
    {
        this.context = context;
        this.playerList = playerList;
    }

    @NonNull
    @Override
    public ApprovePlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_player_infor, parent, false);
        return new ApprovePlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApprovePlayerViewHolder holder, int position) {
        Player player = playerList.get(position);
        holder.txtName.setText(player.getName());
        holder.txtEmail.setText(String.valueOf(player.getEmail()));
        holder.imgAvatar.setImageResource(player.getAvatarResourceId());

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, DisableMemberInfor.class);
            intent.putExtra("name", player.getName());
            intent.putExtra("email", player.getEmail());
            intent.putExtra("avatar", player.getAvatarResourceId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    public static class ApprovePlayerViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView txtName, txtEmail;

        public ApprovePlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtName = itemView.findViewById(R.id.txtName);
            txtEmail = itemView.findViewById(R.id.txtEmail);
        }
    }
}