package com.example.tlupickleball.network.service;

import com.example.tlupickleball.model.Account;
import com.example.tlupickleball.network.api_model.auth.EmailVerificationRequest;
import com.example.tlupickleball.network.api_model.auth.EmailVerificationResponse;
import com.example.tlupickleball.network.api_model.auth.GenericResponse;
import com.example.tlupickleball.network.api_model.auth.LoginResponse;
import com.example.tlupickleball.network.api_model.auth.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AuthService {
    @POST("auth/login")
    Call<LoginResponse> login(@Body Account account);

    @POST("auth/register")
    Call<RegisterResponse> register(@Body Account account);

    @POST("auth/send-verification")
    Call<GenericResponse> sendVerificationEmail(@Body EmailVerificationRequest request);

    @GET("auth/verify-status/{uid}")
    Call<EmailVerificationResponse> checkEmailVerified(@Path("uid") String uid);
}
