package com.example.tlupickleball.network.api_model.user;

import com.google.gson.annotations.SerializedName;

public class UserRankAndStatus {
    @SerializedName("singleRank")
    private int singleRank;
    @SerializedName("doubleRank")
    private int doubleRank;
    @SerializedName("fundStatus")
    private String fundStatus;

    public UserRankAndStatus(int singleRank, String fundStatus, int doubleRank) {
        this.singleRank = singleRank;
        this.fundStatus = fundStatus;
        this.doubleRank = doubleRank;
    }

    public int getSingleRank() {
        return singleRank;
    }

    public void setSingleRank(int singleRank) {
        this.singleRank = singleRank;
    }

    public int getDoubleRank() {
        return doubleRank;
    }

    public void setDoubleRank(int doubleRank) {
        this.doubleRank = doubleRank;
    }

    public String getFundStatus() {
        if(fundStatus.equals("unpaid")){
            return "Chưa đóng";
        }
        else{
            return "Đã đóng";
        }
    }

    public void setFundStatus(String fundStatus) {
        this.fundStatus = fundStatus;
    }
}
