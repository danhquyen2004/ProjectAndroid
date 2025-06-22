package com.example.tlupickleball.activities.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tlupickleball.activities.LoadingOverlay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class BaseActivity extends AppCompatActivity {
    private LoadingOverlay loadingOverlay;
    protected SharedPreferences sharedPreferences;
    protected static final String PREF_NAME = "LoginPrefs";
    protected static final String KEY_EMAIL = "email";
    protected static final String KEY_PASSWORD = "password";
    protected static final String KEY_REMEMBER = "remember";

    @Override
    protected void onStart() {
        super.onStart();
        loadingOverlay = new LoadingOverlay(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    public void showLoading() {
        if (loadingOverlay != null) loadingOverlay.show();
        else {
            loadingOverlay = new LoadingOverlay(this);
            loadingOverlay.show();
        }
    }

    public void hideLoading() {
        if (loadingOverlay != null) loadingOverlay.hide();
        else {
            loadingOverlay = new LoadingOverlay(this);
            loadingOverlay.hide();
        }
    }
    public static String convertDateFormat(String inputDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = inputFormat.parse(inputDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
