package com.example.tlupickleball.activities.base;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;

import com.example.tlupickleball.network.core.ApiClient;
import com.example.tlupickleball.network.service.UserService;

public class BaseMember extends BaseActivity{
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userService = ApiClient.getClient(this).create(UserService.class);
    }
}
