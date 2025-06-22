package com.example.tlupickleball.network.service;

import com.example.tlupickleball.model.User;
import com.example.tlupickleball.network.api_model.user.UserUpdateRequest;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface UserService {

    @POST("user/submit")
    Call<Void> submitProfile(@Body User user);
    // Cập nhật user (admin)
    @PUT("user/{uid}")
    Call<Void> updateUser(@Path("uid") String uid, @Body UserUpdateRequest request);

    // Lấy thông tin user
    @GET("user/{uid}/profile")
    Call<User> getUserProfileById(@Path("uid") String uid);

    // Lấy danh sách toàn bộ user
    @GET("user")
    Call<List<User>> getAllUsers();

    // Xoá user
    @DELETE("user/{uid}")
    Call<Void> deleteUser(@Path("uid") String uid);
}


