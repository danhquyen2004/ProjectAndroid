package com.example.tlupickleball.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tlupickleball.R;
import com.example.tlupickleball.activities.member_fundpersonal;
import com.example.tlupickleball.model.FixedFund;
import com.example.tlupickleball.model.FundStatusAll;
import com.example.tlupickleball.model.MemberFund;
import com.example.tlupickleball.model.Penalty;
import com.example.tlupickleball.model.User;

import java.util.List;

public class MemberFundAdapter extends RecyclerView.Adapter<MemberFundAdapter.ViewHolder> {
    private Context context;
    private List<FundStatusAll> fundStatusList;
    private List<User> userList;
    private OnUserClickListener listener;

    public MemberFundAdapter(Context context, List<FundStatusAll> fundStatusList, List<User> userList) {
        this.context = context;
        this.fundStatusList = fundStatusList;
        this.userList = userList;
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

    private User getUserById(String userId) {
        for (User user : userList) {
            if (user.getUid().equals(userId)) return user;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FundStatusAll item = fundStatusList.get(position);
        User user = getUserById(item.getUserId());
        if (user != null) {
            holder.tvName.setText(getShortName(user.getFullName()));
            Glide.with(context)
                    .load(user.getAvatarUrl())
                    .placeholder(R.drawable.avatar_1)
                    .into(holder.ivAvatar);
        } else {

            holder.tvName.setText(item.getUserId());
            holder.ivAvatar.setImageResource(R.drawable.avatar_1);
        }
        // Trong onBindViewHolder:
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && user != null) {
                listener.onUserClick(user);
            }
        });

        // Tổng quỹ đã đóng
        int fund = 0;
        if (item.getFixedFund() != null && "paid".equalsIgnoreCase(item.getFixedFund().getStatus())) {
            fund = item.getFixedFund().getAmount();
            holder.tvFund.setText("Quỹ: " + formatCurrency(fund));
            holder.tvFund.setBackgroundResource(R.drawable.button_light_green_bg);
            holder.tvFund.setTextColor(ContextCompat.getColor(context, R.color.green_Dong));
        } else if (item.getFixedFund() != null && "unpaid".equalsIgnoreCase(item.getFixedFund().getStatus())) {
            fund = item.getFixedFund().getAmount();
            holder.tvFund.setText("Quỹ: " + formatCurrency(fund));
            holder.tvFund.setBackgroundResource(R.drawable.button_light_red_bg);
            holder.tvFund.setTextColor(ContextCompat.getColor(context, R.color.red_Dong));
        }


        // Tổng phạt chưa đóng
        long penalty = 0;
        if (item.getPenalty().getTotal() == 0 && item.getPenalty().getUnpaid() == 0) {
            penalty = item.getPenalty().getUnpaid();
            holder.tvPenalty.setText("Phạt: " + formatCurrency(penalty));
            holder.tvPenalty.setBackgroundResource(R.drawable.button_light_green_bg);
            holder.tvPenalty.setTextColor(ContextCompat.getColor(context, R.color.green_Dong));
        } else
            if ("unpaid".equalsIgnoreCase(item.getPenalty().getStatus())) {
                penalty = item.getPenalty().getUnpaid();
                holder.tvPenalty.setText("Phạt: " + formatCurrency(penalty));
                holder.tvPenalty.setBackgroundResource(R.drawable.button_light_red_bg);
                holder.tvPenalty.setTextColor(ContextCompat.getColor(context, R.color.red_Dong));
            } else
                if("paid".equalsIgnoreCase(item.getPenalty().getStatus())) {
                    penalty = item.getPenalty().getUnpaid();
                    holder.tvPenalty.setText("Phạt: " + formatCurrency(penalty));
                    holder.tvPenalty.setBackgroundResource(R.drawable.button_light_green_bg);
                    holder.tvPenalty.setTextColor(ContextCompat.getColor(context, R.color.green_Dong));
                } else
                    if("partial".equalsIgnoreCase(item.getPenalty().getStatus())) {
                        penalty = item.getPenalty().getUnpaid();
                        holder.tvPenalty.setText("Phạt: " + formatCurrency(penalty));
                        holder.tvPenalty.setBackgroundResource(R.drawable.button_light_yellow_bg);
                        holder.tvPenalty.setTextColor(ContextCompat.getColor(context, R.color.yellow_Dong));
                    }

        // Tổng ủng hộ
        holder.tvDonate.setText("Ủng hộ: " + formatCurrency(item.getTotalDonation()));
        holder.tvDonate.setBackgroundResource(R.drawable.button_light_green_bg);
        holder.tvDonate.setTextColor(ContextCompat.getColor(context, R.color.green_Dong));
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

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    public void setOnUserClickListener(OnUserClickListener listener) {
        this.listener = listener;
    }

    private String formatCurrency(long amount) {
        return String.format("%,d", amount).replace(',', '.') + "đ";
    }

    private String getShortName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) return "";
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) return parts[0];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            sb.append(parts[i].charAt(0)).append(".");
        }
        sb.append(parts[parts.length - 1]);
        return sb.toString();
    }
}

