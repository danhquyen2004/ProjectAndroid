package com.example.tlupickleball.network.service;

import com.example.tlupickleball.model.Matches;
import com.example.tlupickleball.network.api_model.match.MatchResponse;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Path;
import retrofit2.http.Body;

public interface MatchService {

    @GET("/matches/by-day")
    Call<MatchResponse> getMatchesByDay(
                                         @Query("date") String date,
                                         @Query("page") int page,
                                         @Query("pageSize") int pageSize
    );

    @GET("/matches/by-day/user")
    Call<MatchResponse> getMatchesByDayAndUser(
                                                @Query("date") String date,
                                                @Query("userId") String userId,
                                                @Query("page") int page,
                                                @Query("pageSize") int pageSize
    );

    @POST("/matches")
    Call<Void> createMatch(@Body Matches match);

    @PUT("/matches/{matchId}/scores")
    Call<Void> updateMatchScores(@Path("matchId") String matchId, @Body Matches match);

    @DELETE("/matches/{matchId}") // Đã sửa :matchId thành {matchId}
    Call<Void> deleteMatch(@Path("matchId") String matchId);
}