package com.example.tlupickleball.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlupickleball.R;
import com.example.tlupickleball.activities.member_fundpersonal;
import com.example.tlupickleball.model.FixedFund;
import com.example.tlupickleball.model.FundStatusAll;
import com.example.tlupickleball.model.MemberFund;
import com.example.tlupickleball.model.Penalty;

import java.util.List;

public class MemberFundAdapter extends RecyclerView.Adapter<MemberFundAdapter.ViewHolder> {
    private Context context;
    private List<FundStatusAll> fundStatusList;

    public MemberFundAdapter(Context context, List<FundStatusAll> fundStatusList) {
        this.context = context;
        this.fundStatusList = fundStatusList;
    }

    public void setData(List<FundStatusAll> fundStatusList) {
        this.fundStatusList = fundStatusList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_member_fund, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FundStatusAll item = fundStatusList.get(position);
        holder.tvName.setText(item.getUserId());

        // Tổng quỹ đã đóng
        int fund = 0;
        if (item.getFixedFund() != null) {
            for (FixedFund f : item.getFixedFund()) {
                if ("paid".equalsIgnoreCase(f.getStatus())) {
                    fund += f.getAmount();
                }
            }
        }
        holder.tvFund.setText("Quỹ: " + formatCurrency(fund));

        // Tổng phạt chưa đóng
        int penalty = 0;
        if (item.getPenalty() != null) {
            for (Penalty p : item.getPenalty()) {
                penalty += p.getUnpaid();
            }
        }
        holder.tvPenalty.setText("Phạt: " + formatCurrency(penalty));

        // Tổng ủng hộ
        holder.tvDonate.setText("Ủng hộ: " + formatCurrency(item.getTotalDonation()));
    }

    @Override
    public int getItemCount() {
        return fundStatusList != null ? fundStatusList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        com.google.android.material.imageview.ShapeableImageView ivAvatar;
        TextView tvName, tvFund, tvPenalty, tvDonate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.imageMemberAvatar);
            tvName = itemView.findViewById(R.id.tvMemberName);
            tvFund = itemView.findViewById(R.id.tvMemberFund);
            tvPenalty = itemView.findViewById(R.id.tvMemberPenalty);
            tvDonate = itemView.findViewById(R.id.tvMemberDonate);
        }
    }

    private String formatCurrency(long amount) {
        return String.format("%,d", amount).replace(',', '.') + "đ";
    }
}

