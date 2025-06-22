package com.example.tlupickleball.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlupickleball.R;
import com.example.tlupickleball.adapters.PlayerSelectionAdapter;
import com.example.tlupickleball.model.User;
import com.example.tlupickleball.network.api_model.user.UserListResponse;
import com.example.tlupickleball.network.core.ApiClient;
import com.example.tlupickleball.network.service.UserService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerSelectionDialog extends DialogFragment implements PlayerSelectionAdapter.OnPlayerSelectedListener {

    private RecyclerView playersRecyclerView;
    private EditText playerSearchEditText;
    private PlayerSelectionAdapter playerSelectionAdapter;
    private List<User> allUsers = new ArrayList<>();
    private PlayerSelectedListener listener;
    private UserService userService;

    public interface PlayerSelectedListener {
        void onPlayerSelected(User player, int playerSlot);
    }

    private static final String ARG_PLAYER_SLOT = "player_slot";

    public static PlayerSelectionDialog newInstance(int playerSlot) {
        PlayerSelectionDialog dialog = new PlayerSelectionDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_PLAYER_SLOT, playerSlot);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userService = ApiClient.getClient(getContext()).create(UserService.class);
        fetchAllUsers();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_player, null);
        builder.setView(view);

        playersRecyclerView = view.findViewById(R.id.playersRecyclerView);
        playerSearchEditText = view.findViewById(R.id.playerSearchEditText);

        playerSelectionAdapter = new PlayerSelectionAdapter(new ArrayList<>(), this);
        playersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        playersRecyclerView.setAdapter(playerSelectionAdapter);

        playerSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                playerSelectionAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return builder.create();
    }

    public void setPlayerSelectedListener(PlayerSelectedListener listener) {
        this.listener = listener;
    }

    private void fetchAllUsers() {
        userService.getAllUsers().enqueue(new Callback<UserListResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserListResponse> call, @NonNull Response<UserListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allUsers = response.body().getUsers();
                    if (playerSelectionAdapter != null) {
                        playerSelectionAdapter.updateList(allUsers);
                    }
                } else {
                    Toast.makeText(getContext(), "Không thể lấy danh sách người dùng: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserListResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Lỗi khi lấy danh sách người dùng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPlayerSelected(User player) {
        if (listener != null && getArguments() != null) {
            int playerSlot = getArguments().getInt(ARG_PLAYER_SLOT, -1);
            listener.onPlayerSelected(player, playerSlot);
        }
        dismiss();
    }
}