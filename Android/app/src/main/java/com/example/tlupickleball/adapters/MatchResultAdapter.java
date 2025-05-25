package com.example.tlupickleball.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlupickleball.R;
import com.example.tlupickleball.model.MatchResult;

import java.util.List;

public class MatchResultAdapter extends RecyclerView.Adapter<MatchResultAdapter.MatchViewHolder> {

    private List<MatchResult> matchList;

    public MatchResultAdapter(List<MatchResult> matchList) {
        this.matchList = matchList;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_user_match_card, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        MatchResult match = matchList.get(position);
        holder.player1.setText(match.getPlayer1());
        holder.player2.setText(match.getPlayer2());
        holder.score.setText(match.getScore());
    }

    @Override
    public int getItemCount() {
        return matchList.size();
    }

    public static class MatchViewHolder extends RecyclerView.ViewHolder {
        TextView player1, score, player2;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            player1 = itemView.findViewById(R.id.tvPlayer1);
            score = itemView.findViewById(R.id.tvScore);
            player2 = itemView.findViewById(R.id.tvPlayer2);
        }
    }
}
