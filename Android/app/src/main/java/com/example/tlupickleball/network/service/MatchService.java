package com.example.tlupickleball.network.service;

import com.example.tlupickleball.model.Matches;
import com.example.tlupickleball.network.api_model.match.MatchResponse;
import com.example.tlupickleball.network.api_model.match.CreateMatchRequest;
import com.example.tlupickleball.network.api_model.match.CreateMatchResponse; // Thêm import này

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Path;

public interface MatchService {

    @GET("matches/by-day")
    Call<MatchResponse> getMatchesByDay(
            @Query("date") String date,
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );

    @GET("matches/by-day/user")
    Call<MatchResponse> getMatchesByDayAndUser(
            @Query("date") String date,
            @Query("userId") String userId,
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );

    @POST("matches")
    Call<CreateMatchResponse> createMatch(@Body CreateMatchRequest request);
    @PUT("matches/{matchId}/scores")
    Call<Void> updateMatchScores(@Path("matchId") String matchId, Matches match);
    @DELETE("matches/{matchId}")
    Call<Void> deleteMatch(@Path("matchId") String matchId);
}