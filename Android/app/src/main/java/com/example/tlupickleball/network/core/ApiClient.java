package com.example.tlupickleball.network.core;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import org.json.JSONObject;

import okhttp3.*;

public class ApiClient {
    public static final OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.get("application/json");

    public static Request.Builder postRequest(String token, String url, JSONObject body) {
        return new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .post(RequestBody.create(body.toString(), JSON));
    }
    public static Request.Builder getRequest(String token, String url) {
        return new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .get();
    }

    public static Request.Builder putRequest(String token, String url, JSONObject body) {
        return new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .put(RequestBody.create(body.toString(), JSON));
    }

    public static Request.Builder deleteRequest(String token, String url) {
        return new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .delete();
    }
}