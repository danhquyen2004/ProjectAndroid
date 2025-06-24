package com.example.tlupickleball.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log; // Import Log
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

import java.io.IOException; // Import IOException
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerSelectionDialog extends DialogFragment implements PlayerSelectionAdapter.OnPlayerSelectedListener {

    private RecyclerView playersRecyclerView;
    private PlayerSelectionAdapter playerSelectionAdapter;
    private EditText playerSearchEditText;
    private List<User> allUsers = new ArrayList<>(); // To store all users fetched from API
    private OnPlayerSelectedInDialogListener listener;
    private int playerSlot;
    private ArrayList<String> excludedPlayerIds;

    // Interface để gửi kết quả về Fragment cha
    public interface OnPlayerSelectedInDialogListener {
        void onPlayerSelected(User player, int playerSlot);
    }

    public void setOnPlayerSelectedInDialogListener(OnPlayerSelectedInDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playerSlot = getArguments().getInt("playerSlot", -1);
            excludedPlayerIds = getArguments().getStringArrayList("excludedPlayerIds");
        }
        if (excludedPlayerIds == null) {
            excludedPlayerIds = new ArrayList<>();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_player, null); // Sử dụng dialog_add_player.xml

        playersRecyclerView = view.findViewById(R.id.playersRecyclerView);
        playerSearchEditText = view.findViewById(R.id.playerSearchEditText);

        playerSelectionAdapter = new PlayerSelectionAdapter(new ArrayList<>(), this); // this fragment is the listener
        playersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        playersRecyclerView.setAdapter(playerSelectionAdapter);

        // Fetch users when the dialog is created
        fetchPlayers();

        // Setup search functionality
        playerSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                playerSelectionAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not used
            }
        });

        builder.setView(view)
                .setTitle("Chọn người chơi");
        return builder.create();
    }

    private void fetchPlayers() {
        if (getContext() == null) {
            Log.e("PlayerSelectionDialog", "Context is null, cannot fetch players.");
            return;
        }

        UserService userService = ApiClient.getClient(getContext()).create(UserService.class);
        userService.getAllUsers().enqueue(new Callback<UserListResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserListResponse> call, @NonNull Response<UserListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {


                    List<User> availablePlayers = new ArrayList<>();
                    for (User user : response.body().getUsers()) {
                        if (!excludedPlayerIds.contains(user.getUid())) {
                            availablePlayers.add(user);
                        }
                    }

                    allUsers.clear();
                    allUsers.addAll(availablePlayers);
                    playerSelectionAdapter.updateList(allUsers);

                    if (playerSearchEditText != null) {
                        playerSelectionAdapter.filter(playerSearchEditText.getText().toString());
                    }
                } else {
                    // Xử lý lỗi API, ví dụ: hiển thị Toast hoặc Log
                    String errorBody = "Không có chi tiết lỗi.";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e("PlayerSelectionDialog", "Lỗi đọc errorBody: " + e.getMessage());
                    }
                    Log.e("PlayerSelectionDialog", "Lỗi phản hồi API: " + response.code() + " - " + errorBody);
                    Toast.makeText(getContext(), "Không thể lấy danh sách người dùng: " + response.code() + " - " + errorBody, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserListResponse> call, @NonNull Throwable t) {
                // Xử lý lỗi mạng hoặc lỗi không mong muốn
                Log.e("PlayerSelectionDialog", "Lỗi khi lấy danh sách người dùng (mạng hoặc không mong muốn): " + t.getMessage(), t); // Log cả Throwable
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onPlayerSelected(User player) {
        // Callback từ Adapter khi một người chơi được chọn
        if (listener != null) {
            listener.onPlayerSelected(player, playerSlot); // Truyền playerSlot về Fragment cha
        }
        dismiss(); // Đóng dialog sau khi chọn
    }
}