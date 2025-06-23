package com.example.tlupickleball.network.service;

import com.example.tlupickleball.model.User;
import com.example.tlupickleball.model.UserRank;
import com.example.tlupickleball.network.api_model.user.UserListResponse;
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

    // Lấy danh sách phê duyệt user
    @GET("user/pending-users?limit=0")
    Call<UserListResponse> getPendingUsers();
    // Lấy danh sách vo hieu hoa
    @GET("user/disabled-users?limit=0")
    Call<UserListResponse> getDisableUsers();
    // Lấy danh sách toàn bộ user
    @GET("user/approved-users?limit=0")
    Call<UserListResponse> getAllUsers();
    // Phê duyệt user
    @POST("user/{uid}/approve")
    Call<Void> approveUser(@Path("uid") String uid);
    // Từ chối hoá user
    @POST("user/{uid}/reject")
    Call<Void> rejectUser(@Path("uid") String uid);
    // Vô hiệu hoá user
    @POST("user/disable-user/{uid}")
    Call<Void> disableUser(@Path("uid") String uid);
    // Kích hoạt user
    @POST("user/enable-user/{uid}")
    Call<Void> enableUser(@Path("uid") String uid);
    // Xoá user
    @DELETE("user/{uid}")
    Call<Void> deleteUser(@Path("uid") String uid);
    // Lấy top user single rank
    @GET("user/rankings/single")
    Call<List<UserRank>> getTopSingleRank();
    // Lấy top user duo rank
    @GET("user/rankings/double")
    Call<List<UserRank>> getTopDuoRank();
}


