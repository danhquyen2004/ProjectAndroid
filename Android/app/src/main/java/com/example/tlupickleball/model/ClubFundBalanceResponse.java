package com.example.tlupickleball.model;

import com.google.gson.annotations.SerializedName;

public class ClubFundBalanceResponse {
    @SerializedName("clubFundBalance")
    private long clubFundBalance;

    public long getClubFundBalance() {
        return clubFundBalance;
    }

    public void setClubFundBalance(long clubFundBalance) {
        this.clubFundBalance = clubFundBalance;
    }
}
