package com.example.tlupickleball.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tlupickleball.R;
import com.example.tlupickleball.model.Matches;

import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    private List<Matches> matches;

    public MatchAdapter(List<Matches> matches) {
        this.matches = matches;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_match_result, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        Matches match = matches.get(position);
        holder.tvPlayer1.setText(match.getPlayer1Name());
        holder.imageLeftAvatar.setImageResource(match.getPlayer1Avatar());
        holder.tvScore.setText(match.getScore());
        holder.imageRightAvatar.setImageResource(match.getPlayer2Avatar());
        holder.tvPlayer2.setText(match.getPlayer2Name());
        holder.tvMatchTime.setText(match.getMatchTime());
        holder.tvMatchStatus.setText(match.getMatchStatus());

        switch (match.getMatchStatus()) {
            case "Đang diễn ra":
                holder.tvMatchStatus.setTextColor(holder.itemView.getContext().getColor(R.color.green));
                break;
            case "Đã kết thúc":
                holder.tvMatchStatus.setTextColor(holder.itemView.getContext().getColor(R.color.red));
                break;
            case "Sắp diễn ra":
                holder.tvMatchStatus.setTextColor(holder.itemView.getContext().getColor(R.color.orange));
                break;
            default:
                holder.tvMatchStatus.setTextColor(holder.itemView.getContext().getColor(android.R.color.black));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    public static class MatchViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlayer1;
        ImageView imageLeftAvatar;
        TextView tvScore;
        ImageView imageRightAvatar;
        TextView tvPlayer2;
        TextView tvMatchTime;
        TextView tvMatchStatus;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlayer1 = itemView.findViewById(R.id.tvPlayer1);
            imageLeftAvatar = itemView.findViewById(R.id.image_left_avatar);
            tvScore = itemView.findViewById(R.id.tvScore);
            imageRightAvatar = itemView.findViewById(R.id.image_right_avatar);
            tvPlayer2 = itemView.findViewById(R.id.tvPlayer2);
            tvMatchTime = itemView.findViewById(R.id.tvMatchTime);
            tvMatchStatus = itemView.findViewById(R.id.tvMatchStatus);
        }
    }
}