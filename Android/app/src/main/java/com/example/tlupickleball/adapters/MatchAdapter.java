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
import com.bumptech.glide.Glide;
import com.example.tlupickleball.R;
import com.example.tlupickleball.model.Matches;

import java.util.ArrayList;
import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    // Danh sách các trận đấu mà Adapter đang hiển thị
    private List<Matches> matchesList;
    private final OnMatchClickListener listener;

    public interface OnMatchClickListener {
        void onMatchClicked(Matches match);
    }

    public MatchAdapter(List<Matches> initialMatches, OnMatchClickListener listener) {
        // Tạo một bản sao của danh sách để tránh các vấn đề về tham chiếu
        this.matchesList = new ArrayList<>(initialMatches);
        this.listener = listener;
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
        Matches match = matchesList.get(position);
        holder.bind(match, listener); // Gửi dữ liệu và listener vào ViewHolder
    }

    @Override
    public int getItemCount() {
        return matchesList.size();
    }

    /**
     * Đây là phương thức quan trọng nhất để cập nhật dữ liệu cho Adapter.
     * Nó sẽ xóa sạch danh sách cũ, thêm vào danh sách mới và thông báo cho RecyclerView
     * để vẽ lại toàn bộ giao diện.
     * @param newMatches Danh sách các trận đấu mới cần hiển thị.
     */
    public void updateMatches(List<Matches> newMatches) {
        this.matchesList.clear();
        this.matchesList.addAll(newMatches);
        notifyDataSetChanged(); // Thông báo cho RecyclerView rằng dữ liệu đã thay đổi hoàn toàn
    }


    // ViewHolder Class
    public static class MatchViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlayer1_p1_fullname, tvPlayer1_p2_fullname, tvScore, tvPlayer2_p1_fullname, tvPlayer2_p2_fullname, tvMatchTime, tvMatchStatus;
        ImageView imageTeam1Player1Avatar, imageTeam1Player2Avatar, imageTeam2Player1Avatar, imageTeam2Player2Avatar;
        LinearLayout layoutTeam1Player2, layoutTeam2Player2;

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

        // Phương thức bind giúp code trong onBindViewHolder gọn gàng hơn
        public void bind(final Matches match, final OnMatchClickListener listener) {
            String[] team1Names = match.getPlayer1Name().split(" & ");
            tvPlayer1_p1_fullname.setText(getAbbreviatedName(team1Names.length > 0 ? team1Names[0] : ""));
            String[] team2Names = match.getPlayer2Name().split(" & ");
            tvPlayer2_p1_fullname.setText(getAbbreviatedName(team2Names.length > 0 ? team2Names[0] : ""));

            Glide.with(itemView.getContext()).load(match.getPlayer1AvatarUrl1()).circleCrop().placeholder(R.drawable.avatar_1).into(imageTeam1Player1Avatar);
            Glide.with(itemView.getContext()).load(match.getPlayer2AvatarUrl1()).circleCrop().placeholder(R.drawable.avatar_1).into(imageTeam2Player1Avatar);

            if (match.isDoublesMatch()) {
                layoutTeam1Player2.setVisibility(View.VISIBLE);
                layoutTeam2Player2.setVisibility(View.VISIBLE);
                tvPlayer1_p2_fullname.setText(team1Names.length > 1 ? getAbbreviatedName(team1Names[1]) : "");
                tvPlayer2_p2_fullname.setText(team2Names.length > 1 ? getAbbreviatedName(team2Names[1]) : "");
                Glide.with(itemView.getContext()).load(match.getPlayer1AvatarUrl2()).circleCrop().placeholder(R.drawable.avatar_1).into(imageTeam1Player2Avatar);
                Glide.with(itemView.getContext()).load(match.getPlayer2AvatarUrl2()).circleCrop().placeholder(R.drawable.avatar_1).into(imageTeam2Player2Avatar);
            } else {
                layoutTeam1Player2.setVisibility(View.GONE);
                layoutTeam2Player2.setVisibility(View.GONE);
            }

            tvScore.setText(match.getScore());
            tvMatchTime.setText(match.getMatchTime());
            tvMatchStatus.setText(match.getMatchStatus());

            int statusColor;
            switch (match.getMatchStatus()) {
                case "Đang diễn ra":
                    statusColor = ContextCompat.getColor(itemView.getContext(), R.color.green);
                    break;
                case "Sắp diễn ra":
                    statusColor = ContextCompat.getColor(itemView.getContext(), R.color.orange);
                    break;
                case "Đã kết thúc":
                    statusColor = ContextCompat.getColor(itemView.getContext(), R.color.red);
                    break;
                default:
                    statusColor = ContextCompat.getColor(itemView.getContext(), android.R.color.darker_gray);
                    break;
            }
            tvMatchStatus.setTextColor(statusColor);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMatchClicked(match);
                }
            });
        }

        private String getAbbreviatedName(String fullName) {
            if (fullName == null || fullName.trim().isEmpty()) return "";
            String[] parts = fullName.trim().split("\\s+");
            if (parts.length == 0) return "";
            if (parts.length == 1) return parts[0];

            StringBuilder abbreviated = new StringBuilder();
            for (int i = 0; i < parts.length - 1; i++) {
                if (!parts[i].isEmpty()) {
                    abbreviated.append(parts[i].charAt(0)).append(".");
                }
            }
            abbreviated.append(parts[parts.length - 1]);
            return abbreviated.toString();
        }
    }
}