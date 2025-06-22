package com.example.tlupickleball.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout; // Import LinearLayout
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tlupickleball.R;
import com.example.tlupickleball.model.Matches;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions; // Để có hiệu ứng chuyển đổi

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

        // --- Cập nhật hiển thị tên người chơi/đội ---
        // Xử lý tên cho đội 1
        String[] team1Names = match.getPlayer1Name().split(" & ");
        holder.tvPlayer1_p1_fullname.setText(getAbbreviatedName(team1Names.length > 0 ? team1Names[0] : ""));

        // Xử lý tên cho đội 2
        String[] team2Names = match.getPlayer2Name().split(" & ");
        holder.tvPlayer2_p1_fullname.setText(getAbbreviatedName(team2Names.length > 0 ? team2Names[0] : ""));

        // --- Load Avatars bằng Glide ---
        Glide.with(holder.itemView.getContext())
                .load(match.getPlayer1AvatarUrl1())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.imageTeam1Player1Avatar);

        Glide.with(holder.itemView.getContext())
                .load(match.getPlayer2AvatarUrl1())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.imageTeam2Player1Avatar);

        // Xử lý hiển thị cho đấu đôi
        if (match.isDoublesMatch()) { // Sử dụng isDoublesMatch từ model
            holder.layoutTeam1Player2.setVisibility(View.VISIBLE);
            holder.layoutTeam2Player2.setVisibility(View.VISIBLE);

            // Tên người chơi thứ 2 đội 1
            holder.tvPlayer1_p2_fullname.setText(team1Names.length > 1 ? getAbbreviatedName(team1Names[1]) : "");
            // Tên người chơi thứ 2 đội 2
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

    // Hàm mới để tạo tên viết tắt (P.G.Quỳnh)
    private String getAbbreviatedName(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            return "";
        }
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 0) {
            return "";
        }
        StringBuilder abbreviated = new StringBuilder();
        // Lấy chữ cái đầu của tất cả các phần trừ phần cuối cùng (tên cuối)
        for (int i = 0; i < parts.length - 1; i++) {
            if (!parts[i].isEmpty()) {
                abbreviated.append(parts[i].charAt(0)).append(".");
            }
        }
        // Thêm tên cuối
        abbreviated.append(parts[parts.length - 1]);
        return abbreviated.toString();
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    public static class MatchViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlayer1_p1_fullname;
        ImageView imageTeam1Player1Avatar;
        LinearLayout layoutTeam1Player2;
        TextView tvPlayer1_p2_fullname;
        ImageView imageTeam1Player2Avatar;

        TextView tvScore;

        TextView tvPlayer2_p1_fullname;
        ImageView imageTeam2Player1Avatar;
        LinearLayout layoutTeam2Player2;
        TextView tvPlayer2_p2_fullname;
        ImageView imageTeam2Player2Avatar;

        TextView tvMatchTime;
        TextView tvMatchStatus;

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