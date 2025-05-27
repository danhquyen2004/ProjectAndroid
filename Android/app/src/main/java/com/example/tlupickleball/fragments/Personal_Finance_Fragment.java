package com.example.tlupickleball.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.tlupickleball.R;


public class Personal_Finance_Fragment extends Fragment {

    private View rootView;
    private ImageButton btnDonate, btnFund, btnFine;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_personal_finance, container, false);

        initViews();
        return rootView;
    }

    private void initViews() {
        btnDonate = rootView.findViewById(R.id.btn_donate);
        btnFund = rootView.findViewById(R.id.btn_contribute_fund);
        btnFine = rootView.findViewById(R.id.btn_fine);

        View.OnClickListener qrClickListener = v -> showQrPopup();

        btnDonate.setOnClickListener(qrClickListener);
        btnFund.setOnClickListener(qrClickListener);
        btnFine.setOnClickListener(qrClickListener);
    }

    private void showQrPopup() {
        if (getContext() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.qr_finance, null);

        ImageView imageQr = dialogView.findViewById(R.id.image_qr);
        imageQr.setImageResource(R.drawable.qr_code);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }
}