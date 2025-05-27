package com.example.tlupickleball.activities;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlupickleball.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.tlupickleball.adapters.PlayerAdapter;
import com.example.tlupickleball.adapters.SearchAdapter;
import com.google.android.material.datepicker.MaterialDatePicker;

public class record_the_match extends AppCompatActivity {

    TextView item1,item2, select, txt_slot1, txt_slot2, txt_slot3, txt_slot4, tvSoSet, datetime ;

    ColorStateList def;
    View slot1, slot2, slot3, slot4;
    RadioGroup radGroup;
    EditText edtTeam1, edtTeam2;
    Set<String> selectedPlayers = new HashSet<>();

    List<String> allPlayers = Arrays.asList("Nguyễn Văn A", "Trần Thị B", "Lê Quốc C", "Đinh Hoàng D",
            "Phạm Thị E", "Nguyễn Văn F", "Trần Thị G", "Lê Quốc H", "Đinh Hoàng I", "Phạm Thị J",
            "Nguyễn Văn K", "Trần Thị L", "Lê Quốc M", "Đinh Hoàng N", "Phạm Thị O");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_record_the_match);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        item1 = findViewById(R.id.item1);
        item1.setText("Đấu đơn");
        item2 = findViewById(R.id.item2);
        item2.setText("Đấu đôi");
        select = findViewById(R.id.select);
        def = item2.getTextColors();
        txt_slot1 = findViewById(R.id.id_txtslot1);
        txt_slot2 = findViewById(R.id.id_txtslot2);
        txt_slot3 = findViewById(R.id.id_txtslot3);
        txt_slot4 = findViewById(R.id.id_txtslot4);
        slot1 = findViewById(R.id.slot1);
        slot2 = findViewById(R.id.slot2);
        slot3 = findViewById(R.id.slot3);
        slot4 = findViewById(R.id.slot4);
        radGroup = findViewById(R.id.radGroup_status);
        tvSoSet = findViewById(R.id.tvSoSet);
        edtTeam1 = findViewById(R.id.edt_resultteam1);
        edtTeam2 = findViewById(R.id.edt_resultteam2);
        datetime = findViewById(R.id.txt_datetime);


        slot2.setVisibility(View.GONE);
        slot3.setVisibility(View.GONE);
        item1.setOnClickListener(this::onClick);
        item2.setOnClickListener(this::onClick);

        slot1.setOnClickListener(v -> showSearchPopup(this, txt_slot1));
        slot2.setOnClickListener(v -> showSearchPopup(this, txt_slot2));
        slot3.setOnClickListener(v -> showSearchPopup(this, txt_slot3));
        slot4.setOnClickListener(v -> showSearchPopup(this, txt_slot4));

        datetime.setOnClickListener(v -> showDatePicker());

        tvSoSet.setVisibility(View.GONE);
        edtTeam1.setVisibility(View.GONE);
        edtTeam2.setVisibility(View.GONE);
        radGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rad_ongoing){
                    tvSoSet.setVisibility(View.GONE);
                    edtTeam1.setVisibility(View.GONE);
                    edtTeam2.setVisibility(View.GONE);
                } else {
                    tvSoSet.setVisibility(View.VISIBLE);
                    edtTeam1.setVisibility(View.VISIBLE);
                    edtTeam2.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void onClick(View v) {
        if (v.getId() == R.id.item1) {
            slot2.setVisibility(View.GONE);
            slot3.setVisibility(View.GONE);
            select.animate().x(0).setDuration(100);
            item1.setTextColor(Color.WHITE);
            item2.setTextColor(def);

        } else if (v.getId() == R.id.item2) {
            slot2.setVisibility(View.VISIBLE);
            slot3.setVisibility(View.VISIBLE);
            item1.setTextColor(def);
            item2.setTextColor(Color.WHITE);
            int size = item2.getWidth();
            select.animate().x(size).setDuration(100);
        }
    }

    private void showSearchPopup(Context context, final TextView targetSlot) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.search_player, null);
        EditText searchEditText = view.findViewById(R.id.search_edit_text);
        RecyclerView recyclerView = view.findViewById(R.id.search_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));


        List<String> availablePlayers = new ArrayList<>();
        for (String p : allPlayers) {
            if (!selectedPlayers.contains(p)) {
                availablePlayers.add(p);
            }
        }

        AlertDialog dialog = builder.setView(view).create();

        SearchAdapter adapter = new SearchAdapter(availablePlayers, new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String playerName) {
                String oldName = targetSlot.getText().toString();
                if (!oldName.isEmpty()) selectedPlayers.remove(oldName);

                targetSlot.setText(playerName);
                selectedPlayers.add(playerName);
                dialog.dismiss();
            }
        });

        recyclerView.setAdapter(adapter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> filtered = new ArrayList<>();
                for (String player : availablePlayers) {
                    if (player.toLowerCase().contains(s.toString().toLowerCase())) {
                        filtered.add(player);
                    }
                }
                adapter.updateSearchResults(filtered);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        dialog.show();
    }



    public void showDatePicker() {
        MaterialDatePicker<Long> datePicker =
                MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Chọn ngày")
                        .build();

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            String selectedDate = datePicker.getHeaderText();
            datetime.setText(selectedDate); // giả sử bạn có edtDate
        });
    }

}