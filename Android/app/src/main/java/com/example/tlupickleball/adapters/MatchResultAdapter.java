package com.example.tlupickleball.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tlupickleball.R;
import com.example.tlupickleball.model.MatchResult;
import com.google.android.material.imageview.ShapeableImageView;
import java.util.List;

public class MatchResultAdapter extends RecyclerView.Adapter<MatchResultAdapter.ViewHolder> {
    private List<MatchResult> results;

    public MatchResultAdapter(List<MatchResult> results) {
        this.results = results;
    }

    // Method để cập nhật danh sách khi chọn ngày khác
    public void updateData(List<MatchResult> newResults) {
        this.results = newResults;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlayer1, tvPlayer2, tvScore;
        ShapeableImageView imageLeftAvatar, imageRightAvatar;

        public ViewHolder(View view) {
            super(view);
            tvPlayer1 = view.findViewById(R.id.tvPlayer1);
            tvPlayer2 = view.findViewById(R.id.tvPlayer2);
            tvScore = view.findViewById(R.id.tvScore);
            imageLeftAvatar = view.findViewById(R.id.image_left_avatar);
            imageRightAvatar = view.findViewById(R.id.image_right_avatar);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match_result, parent, false);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MatchResult m = results.get(position);
        holder.tvPlayer1.setText(m.player1);
        holder.tvPlayer2.setText(m.player2);

        if (m.score == null || m.score.trim().isEmpty()) {
            holder.tvScore.setText("-");
        } else {
            holder.tvScore.setText(m.score);
        }

    }



    @Override
    public int getItemCount() {
        return results.size();
    }
}
