package com.example.tlupickleball.apis;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.tlupickleball.activities.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.*;

public class UserApi {

    public static void sendSubmitAccount(Context context, String token, String phone, String password) {
        String url = "https://us-central1-tlu-pickleball-459716.cloudfunctions.net/submitAccount";

        OkHttpClient client = new OkHttpClient();
        JSONObject body = new JSONObject();
        try {
            body.put("phoneNumber", phone);
            body.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .post(RequestBody.create(body.toString(), MediaType.get("application/json")))
                .build();

        client.newCall(request).enqueue(new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("API", "Tạo tài khoản thành công");
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else {
                    Log.e("API", "Lỗi submitAccount: " + response.body().string());
                }
            }

            public void onFailure(Call call, IOException e) {
                Log.e("API", "Lỗi kết nối submitAccount", e);
            }
        });
    }
    public static void sendSubmitProfile(Context context, String token, String name, String dob, String gender) {
        String url = "https://us-central1-tlu-pickleball-459716.cloudfunctions.net/submitProfile";

        OkHttpClient client = new OkHttpClient();
        JSONObject body = new JSONObject();
        try {
            body.put("name", name);
            body.put("dob", dob);
            body.put("gender", gender);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .post(RequestBody.create(body.toString(), MediaType.get("application/json")))
                .build();

        client.newCall(request).enqueue(new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("API", "Gửi hồ sơ thành công");
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else {
                    Log.e("API", "Lỗi gửi hồ sơ: " + response.body().string());
                }
            }

            public void onFailure(Call call, IOException e) {
                Log.e("API", "Lỗi kết nối submitProfile", e);
            }
        });
    }

}
