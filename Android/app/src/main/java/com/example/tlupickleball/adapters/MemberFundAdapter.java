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
import com.example.tlupickleball.model.MemberFund;

import java.util.List;

public class MemberFundAdapter extends RecyclerView.Adapter<MemberFundAdapter.MemberViewHolder> {

    private List<MemberFund> memberList;
    private final Context context;

    public MemberFundAdapter(Context context, List<MemberFund> memberList) {
        this.context = context;
        this.memberList = memberList;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_member_fund, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        MemberFund member = memberList.get(position);
        holder.tvName.setText(member.getName());
        holder.tvFund.setText("Quỹ: " + formatAmount(member.getFund()));
        holder.tvPenalty.setText("Phạt: " + formatAmount(member.getPenalty()));
        holder.tvDonate.setText("Ủng hộ: " + formatAmount(member.getDonate()));

        // Thiết lập sự kiện click cho mỗi item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, member_fundpersonal.class);
            // Nếu muốn truyền thêm thông tin:
            intent.putExtra("name", member.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public void setData(List<MemberFund> newList) {
        this.memberList = newList;
        notifyDataSetChanged(); // Làm mới RecyclerView
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvFund, tvPenalty, tvDonate;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvMemberName);
            tvFund = itemView.findViewById(R.id.tvMemberFund);
            tvPenalty = itemView.findViewById(R.id.tvMemberPenalty);
            tvDonate = itemView.findViewById(R.id.tvMemberDonate);
        }
    }

    private String formatAmount(int amount) {
        return String.format("%,d", amount).replace(',', '.') + "đ";
    }
}

