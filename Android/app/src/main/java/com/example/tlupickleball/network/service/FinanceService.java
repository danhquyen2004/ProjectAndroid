package com.example.tlupickleball.network.service;

import com.example.tlupickleball.model.MemberFund1;
import com.example.tlupickleball.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FinanceService {
    @GET("finance/fund-status/{userId}?month=5&year=2025")
    Call<MemberFund1> financeStatus(@Path("userId") String userId);

    @GET("finance/finance-logs/{userId}?month=5&year=2025")
    Call<Void> financeHistory(@Path("userId") String userId);
//
//    @GET("finance/club/balance")
//    Call<Void> financeTotal(@Path("uid") String uid);

//    @GET("finance/club/summary?month=6&year=2025")
//    Call<Void> financeStatus(@Path("uid") String uid);
//
//    @GET("finance/fund-status/:userId?month=5&year=2025")
//    Call<Void> financeStatus(@Path("uid") String uid);
}
