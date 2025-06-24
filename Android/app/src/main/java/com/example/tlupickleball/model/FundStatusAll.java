package com.example.tlupickleball.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FundStatusAll {
    @SerializedName("userId")
    private String userId;
    @SerializedName("fixedFund")
    private List<FixedFund> fixedFund;
    @SerializedName("penalty")
    private List<Penalty> penalty;
    @SerializedName("totalDonation")
    private long totalDonation;

    public FundStatusAll(String userId, List<FixedFund> fixedFund, List<Penalty> penalty, long totalDonation) {
        this.userId = userId;
        this.fixedFund = fixedFund;
        this.penalty = penalty;
        this.totalDonation = totalDonation;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<FixedFund> getFixedFund() {
        return fixedFund;
    }

    public void setFixedFund(List<FixedFund> fixedFund) {
        this.fixedFund = fixedFund;
    }

    public List<Penalty> getPenalty() {
        return penalty;
    }

    public void setPenalty(List<Penalty> penalty) {
        this.penalty = penalty;
    }

    public long getTotalDonation() {
        return totalDonation;
    }

    public void setTotalDonation(long totalDonation) {
        this.totalDonation = totalDonation;
    }
}
