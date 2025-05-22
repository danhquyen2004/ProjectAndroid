package com.example.tlupickleball.activities;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tlupickleball.R;

public class AllResult_End extends AppCompatActivity implements View.OnClickListener {

    ColorStateList def;
    TextView item1, item2, select;

    Dialog dialog;
    Button btnDialogCancel, btnDialogConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_result_end);

        item1 = findViewById(R.id.item1);
        item1.setText("Cá nhân");
        item2 = findViewById(R.id.item2);
        item2.setText("Tổng quát");

        item1.setOnClickListener( this);
        item2.setOnClickListener( this);

        select = findViewById(R.id.select);
        def = item2.getTextColors();

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.view_dialog_form);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_box));
        dialog.setCancelable(false);

        btnDialogConfirm = dialog.findViewById(R.id.btnDiaLogOK);
        btnDialogCancel = dialog.findViewById(R.id.btnDiaLogCancel);

        btnDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnDialogConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.item1) {
            select.animate().x(0).setDuration(100);
            item1.setTextColor(Color.WHITE);
            item2.setTextColor(def);
            dialog.show();
        } else if (v.getId() == R.id.item2) {
            item1.setTextColor(def);
            item2.setTextColor(Color.WHITE);
            int size = item2.getWidth();
            select.animate().x(size).setDuration(100);
            dialog.show();
        }
    }
}