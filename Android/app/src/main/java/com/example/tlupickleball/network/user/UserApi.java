package com.example.tlupickleball.network.user;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.tlupickleball.activities.base.ApiCallback;
import com.example.tlupickleball.network.core.ApiClient;
import com.example.tlupickleball.activities.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class UserApi {

    public static void submitProfile(Context context, String token, String name, String dob, String gender, ApiCallback callback) {
        String url = "https://us-central1-tlu-pickleball-459716.cloudfunctions.net/submitProfile";

        JSONObject body = new JSONObject();
        try {
            body.put("name", name);
            body.put("dob", dob);
            body.put("gender", gender);
        } catch (JSONException e) {
            callback.onFailure("Invalid profile data");
            callback.onComplete();
            return;
        }

        Request request = ApiClient.postRequest(token, url, body).build();

        ApiClient.client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure("Submit failed: " + response.code());
                }
                callback.onComplete();
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Network error: " + e.getMessage());
                callback.onComplete();
            }
        });
    }
}