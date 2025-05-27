package com.example.tlupickleball.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlupickleball.R;
import com.example.tlupickleball.model.Matches;
import com.google.android.material.imageview.ShapeableImageView;
import java.util.List;
import java.util.ArrayList;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    private Context context;
    private List<Matches> matchList;
    private List<Matches> originalMatchList;

    public MatchAdapter(Context context, List<Matches> matchList) {
        this.context = context;
        this.matchList = matchList;
        this.originalMatchList = new ArrayList<>(matchList != null ? matchList : new ArrayList<>());
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_match_result, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        Matches match = matchList.get(position);

        // HIỆN TẠI VÀ TRƯỚC ĐÂY: Hiển thị tiêu đề ngày nếu cần
        // VỚI THAY ĐỔI NÀY, CHÚNG TA SẼ LUÔN ẨN NÓ ĐI BẰNG CÁCH KHÔNG GỌI setShowDateTitle(true)
        // HOẶC KHÔNG GÁN GIÁ TRỊ showDateTitle TRONG CONSTRUCTOR CỦA Matches NỮA.
        // NHƯNG ĐỂ CHẮC CHẮN NẾU TRƯỜNG ĐÓ VẪN TỒN TẠI, HÃY LUÔN ẨN VIEW NÀY.
        holder.tvDateTitle.setVisibility(View.GONE);


        holder.tvPlayer1.setText(match.getPlayer1Name());
        holder.imageLeftAvatar.setImageResource(match.getPlayer1AvatarResId());
        holder.tvScore.setText(match.getScore());
        holder.imageRightAvatar.setImageResource(match.getPlayer2AvatarResId());
        holder.tvPlayer2.setText(match.getPlayer2Name());
    }

    @Override
    public int getItemCount() {
        return matchList.size();
    }

    public void updateMatches(List<Matches> newMatches) {
        this.originalMatchList.clear();
        if (newMatches != null) {
            this.originalMatchList.addAll(newMatches);
        }
    }

    public void filterMatches(String dateFilter, String tabCriteria) {
        matchList.clear();
        if (originalMatchList == null || originalMatchList.isEmpty()) {
            notifyDataSetChanged();
            return;
        }

        boolean noDateFilter = (dateFilter == null || dateFilter.isEmpty());

        for (Matches match : originalMatchList) {
            boolean dateMatches = noDateFilter || (match.getDateForFilter() != null && match.getDateForFilter().equals(dateFilter));
            boolean tabMatches = true;

            if (tabCriteria != null) {
                if (tabCriteria.equals("Cá nhân")) {
                    tabMatches = dateMatches;
                } else if (tabCriteria.equals("Tổng quát")) {
                    tabMatches = dateMatches; // Vẫn lọc theo ngày nếu bạn muốn ngày được chọn hiển thị trận đấu của ngày đó
                }
            }

            if (dateMatches && tabMatches) {
                matchList.add(match);
            }
        }

        // XÓA HOẶC COMMENT KHỐI CODE NÀY ĐỂ BỎ TIÊU ĐỀ NGÀY
        /*
        String lastDate = null;
        for (int i = 0; i < matchList.size(); i++) {
            Matches currentMatch = matchList.get(i);
            if (currentMatch.getFullDateString() != null && !currentMatch.getFullDateString().equals(lastDate)) {
                currentMatch.setShowDateTitle(true);
                lastDate = currentMatch.getFullDateString();
            } else {
                currentMatch.setShowDateTitle(false);
            }
        }
        */

        notifyDataSetChanged();
    }

    static class MatchViewHolder extends RecyclerView.ViewHolder {
        TextView tvDateTitle;
        TextView tvPlayer1;
        ShapeableImageView imageLeftAvatar;
        TextView tvScore;
        ShapeableImageView imageRightAvatar;
        TextView tvPlayer2;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateTitle = itemView.findViewById(R.id.tvDateTitle);
            tvPlayer1 = itemView.findViewById(R.id.tvPlayer1);
            imageLeftAvatar = itemView.findViewById(R.id.image_left_avatar);
            tvScore = itemView.findViewById(R.id.tvScore);
            imageRightAvatar = itemView.findViewById(R.id.image_right_avatar);
            tvPlayer2 = itemView.findViewById(R.id.tvPlayer2);
        }
    }
}