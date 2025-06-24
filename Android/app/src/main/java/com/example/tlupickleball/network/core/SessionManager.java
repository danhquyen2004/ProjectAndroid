package com.example.tlupickleball.network.core;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "user_session";
    private static final String KEY_ID_TOKEN = "id_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_UID = "uid";

    public static void saveTokens(Context context, String idToken, String refreshToken, String uid) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_ID_TOKEN, idToken)
                .putString(KEY_REFRESH_TOKEN, refreshToken)
                .putString(KEY_UID, uid)
                .apply();
    }

    public static String getIdToken(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(KEY_ID_TOKEN, null);
    }

    public static String getRefreshToken(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(KEY_REFRESH_TOKEN, null);
    }

    public static String getUid(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(KEY_UID, null);
    }

    public static void clearSession(Context context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().clear().apply();
    }

}
