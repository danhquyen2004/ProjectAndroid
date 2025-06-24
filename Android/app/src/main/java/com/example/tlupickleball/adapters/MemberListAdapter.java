package com.example.tlupickleball.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.tlupickleball.R;
import com.example.tlupickleball.activities.MemberControllerInfor;
import com.example.tlupickleball.model.User;

import java.util.List;

public class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.ApprovePlayerViewHolder> {
    private List<User> users;
    private Context context;

    public MemberListAdapter(Context context, List<User> users)
    {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ApprovePlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_player_infor, parent, false);
        return new ApprovePlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApprovePlayerViewHolder holder, int position) {
        User user = users.get(position);
        holder.txtName.setText(user.getFullName());
        holder.txtEmail.setText(String.valueOf(user.getEmail()));
        Glide.with(context)
                .load(user.getAvatarUrl())
                .placeholder(R.drawable.default_avatar) // ảnh mặc định nếu chưa có
                .diskCacheStrategy(DiskCacheStrategy.NONE) // Bỏ qua cache trên đĩa
                .skipMemoryCache(true) // Bỏ qua cache trong bộ nhớ
                .circleCrop()
                .into(holder.imgAvatar);

        holder.itemView.setOnClickListener(v -> {
//            Context context = v.getContext();
//            Intent intent = new Intent(context, MemberControllerInfor.class);
//            intent.putExtra("uid", user.getUid());
//            context.startActivity(intent);
           listener.onMemberClick(user);
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ApprovePlayerViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView txtName, txtEmail;

        public ApprovePlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtName = itemView.findViewById(R.id.txtName);
            txtEmail = itemView.findViewById(R.id.txtEmail);
        }
    }
    public interface OnMemberClickListener {
        void onMemberClick(User user);
    }

    private OnMemberClickListener listener;

    public MemberListAdapter(Context context, List<User> users, OnMemberClickListener listener) {
        this.context = context;
        this.users = users;
        this.listener = listener;
    }
}