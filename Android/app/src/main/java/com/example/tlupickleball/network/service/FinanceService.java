package com.example.tlupickleball.network.service;

import com.example.tlupickleball.model.ClubFundBalanceResponse;
import com.example.tlupickleball.model.ClubSummaryResponse;
import com.example.tlupickleball.model.MemberFund1;
import com.example.tlupickleball.model.User;
import com.example.tlupickleball.model.logClub;
import com.example.tlupickleball.network.api_model.finance.FinanceClubListResponse;
import com.example.tlupickleball.network.api_model.finance.FinanceListResponse;
import com.example.tlupickleball.network.api_model.finance.FinanceUserFundStatus;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FinanceService {
    @GET("finance/fund-status/{userId}")
    Call<MemberFund1> financeStatus(
            @Path("userId") String userId,
            @Query("month") int month,
            @Query("year") int year
    );

    @GET("finance/finance-logs/{userId}")
    Call<FinanceListResponse> financeHistory(
            @Path("userId") String userId,
            @Query("month") int month,
            @Query("year") int year
    );

    @GET("finance/club/balance")
    Call<ClubFundBalanceResponse> financeTotal();

    @GET("finance/club/summary")
    Call<ClubSummaryResponse> financeClubSummary(
            @Query("month") int month,
            @Query("year") int year
    );

    @GET("finance/club/expenses")
    Call<FinanceClubListResponse> financeClubHistory(
            @Query("month") int month,
            @Query("year") int year
    );

    @GET("finance/fund-status-all")
    Call<FinanceUserFundStatus> financeStatusAll(
            @Query("month") int month,
            @Query("year") int year
    );

    @POST("finance/club/expense")
    Call<Void> financeClubExpense(@Body logClub logclub);
}
