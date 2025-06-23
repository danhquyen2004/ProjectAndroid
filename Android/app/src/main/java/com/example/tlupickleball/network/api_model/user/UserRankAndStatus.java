package com.example.tlupickleball.network.api_model.user;

public class UserRankAndStatus {
    private int singleRank;
    private int doubleRank;
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
