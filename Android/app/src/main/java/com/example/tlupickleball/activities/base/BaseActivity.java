package com.example.tlupickleball.activities.base;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tlupickleball.activities.LoadingOverlay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public abstract class BaseActivity extends AppCompatActivity {
    private LoadingOverlay loadingOverlay;

    @Override
    protected void onStart() {
        super.onStart();
        loadingOverlay = new LoadingOverlay(this);
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

    public static String convertToVietnameseDate(String isoDate) {
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat vnFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date date = isoFormat.parse(isoDate);
            return vnFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}
