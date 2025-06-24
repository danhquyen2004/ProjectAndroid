package com.example.tlupickleball.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FundStatusAll {
    @SerializedName("userId")
    private String userId;
    @SerializedName("fixedFund")
    private FixedFund fixedFund;
    @SerializedName("penalty")
    private Penalty penalty;
    @SerializedName("totalDonation")
    private long totalDonation;

    public FundStatusAll(String userId, FixedFund fixedFund, Penalty penalty, long totalDonation) {
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

    public FixedFund getFixedFund() {
        return fixedFund;
    }

    public void setFixedFund(FixedFund fixedFund) {
        this.fixedFund = fixedFund;
    }

    public Penalty getPenalty() {
        return penalty;
    }

    public void setPenalty(Penalty penalty) {
        this.penalty = penalty;
    }

    public long getTotalDonation() {
        return totalDonation;
    }

    public void setTotalDonation(long totalDonation) {
        this.totalDonation = totalDonation;
    }
}
