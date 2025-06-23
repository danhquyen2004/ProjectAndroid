package com.example.tlupickleball.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tlupickleball.R;
import com.example.tlupickleball.model.Matches;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

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

        String[] team1Names = match.getPlayer1Name().split(" & ");
        holder.tvPlayer1_p1_fullname.setText(getAbbreviatedName(team1Names.length > 0 ? team1Names[0] : ""));

        String[] team2Names = match.getPlayer2Name().split(" & ");
        holder.tvPlayer2_p1_fullname.setText(getAbbreviatedName(team2Names.length > 0 ? team2Names[0] : ""));

        Glide.with(holder.itemView.getContext())
                .load(match.getPlayer1AvatarUrl1())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.imageTeam1Player1Avatar);

        Glide.with(holder.itemView.getContext())
                .load(match.getPlayer2AvatarUrl1())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.imageTeam2Player1Avatar);

        if (match.isDoublesMatch()) {
            holder.layoutTeam1Player2.setVisibility(View.VISIBLE);
            holder.layoutTeam2Player2.setVisibility(View.VISIBLE);

            holder.tvPlayer1_p2_fullname.setText(team1Names.length > 1 ? getAbbreviatedName(team1Names[1]) : "");
            holder.tvPlayer2_p2_fullname.setText(team2Names.length > 1 ? getAbbreviatedName(team2Names[1]) : "");

            Glide.with(holder.itemView.getContext())
                    .load(match.getPlayer1AvatarUrl2())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.imageTeam1Player2Avatar);

            Glide.with(holder.itemView.getContext())
                    .load(match.getPlayer2AvatarUrl2())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.imageTeam2Player2Avatar);
        } else {
            holder.layoutTeam1Player2.setVisibility(View.GONE);
            holder.layoutTeam2Player2.setVisibility(View.GONE);
        }

        holder.tvScore.setText(match.getScore());
        holder.tvMatchTime.setText(match.getMatchTime());
        holder.tvMatchStatus.setText(match.getMatchStatus());

        int statusColor;
        switch (match.getMatchStatus()) {
            case "Đang diễn ra":
                statusColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.green);
                break;
            case "Sắp diễn ra":
                // Dùng màu cam (orange) vì màu vàng (yellow) thường khó đọc trên nền trắng
                statusColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.orange);
                break;
            case "Đã kết thúc":
                statusColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.red);
                break;
            default:
                // Màu mặc định nếu trạng thái không khớp
                statusColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.black);
                break;
        }
        holder.tvMatchStatus.setTextColor(statusColor);
    }

    private String getAbbreviatedName(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            return "";
        }
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 0) {
            return "";
        }
        StringBuilder abbreviated = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            if (!parts[i].isEmpty()) {
                abbreviated.append(parts[i].charAt(0)).append(".");
            }
        }
        abbreviated.append(parts[parts.length - 1]);
        return abbreviated.toString();
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    public void updateMatches(List<Matches> newMatches) {
        this.matches.clear();
        this.matches.addAll(newMatches);
        notifyDataSetChanged();
    }

    public static class MatchViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlayer1_p1_fullname, tvPlayer1_p2_fullname;
        ImageView imageTeam1Player1Avatar, imageTeam1Player2Avatar;
        LinearLayout layoutTeam1Player2;

        TextView tvScore;

        TextView tvPlayer2_p1_fullname, tvPlayer2_p2_fullname;
        ImageView imageTeam2Player1Avatar, imageTeam2Player2Avatar;
        LinearLayout layoutTeam2Player2;

        TextView tvMatchTime, tvMatchStatus;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlayer1_p1_fullname = itemView.findViewById(R.id.tvPlayer1_p1_fullname);
            imageTeam1Player1Avatar = itemView.findViewById(R.id.image_team1_player1_avatar);
            layoutTeam1Player2 = itemView.findViewById(R.id.layout_team1_player2);
            tvPlayer1_p2_fullname = itemView.findViewById(R.id.tvPlayer1_p2_fullname);
            imageTeam1Player2Avatar = itemView.findViewById(R.id.image_team1_player2_avatar);

            tvScore = itemView.findViewById(R.id.tvScore);

            tvPlayer2_p1_fullname = itemView.findViewById(R.id.tvPlayer2_p1_fullname);
            imageTeam2Player1Avatar = itemView.findViewById(R.id.image_team2_player1_avatar);
            layoutTeam2Player2 = itemView.findViewById(R.id.layout_team2_player2);
            tvPlayer2_p2_fullname = itemView.findViewById(R.id.tvPlayer2_p2_fullname);
            imageTeam2Player2Avatar = itemView.findViewById(R.id.image_team2_player2_avatar);

            tvMatchTime = itemView.findViewById(R.id.tvMatchTime);
            tvMatchStatus = itemView.findViewById(R.id.tvMatchStatus);
        }
    }
}