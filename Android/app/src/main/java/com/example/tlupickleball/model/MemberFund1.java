package com.example.tlupickleball.model;

import com.google.gson.annotations.SerializedName;

public class MemberFund1 {
    @SerializedName("fixedFund")
    private  FixedFund fixedFund;
    @SerializedName("totalDonation")
    private long totalDonation;
    @SerializedName("totalPenalty")
    private long totalPenalty;
    @SerializedName("totalPenaltyPaid")
    private long totalPenaltyPaid;
    @SerializedName("totalPenaltyUnpaid")
    private long totalPenaltyUnpaid;

    public MemberFund1(FixedFund fixedFund, long totalDonation, long totalPenalty, long totalPenaltyPaid, long totalPenaltyUnpaid) {
        this.fixedFund = fixedFund;
        this.totalDonation = totalDonation;
        this.totalPenalty = totalPenalty;
        this.totalPenaltyPaid = totalPenaltyPaid;
        this.totalPenaltyUnpaid = totalPenaltyUnpaid;
    }

    public FixedFund getFixedFund() {
        return fixedFund;
    }

    public void setFixedFund(FixedFund fixedFund) {
        this.fixedFund = fixedFund;
    }

    public long getTotalDonation() {
        return totalDonation;
    }

    public void setTotalDonation(long totalDonation) {
        this.totalDonation = totalDonation;
    }

    public long getTotalPenalty() {
        return totalPenalty;
    }

    public void setTotalPenalty(long totalPenalty) {
        this.totalPenalty = totalPenalty;
    }

    public long getTotalPenaltyPaid() {
        return totalPenaltyPaid;
    }

    public void setTotalPenaltyPaid(long totalPenaltyPaid) {
        this.totalPenaltyPaid = totalPenaltyPaid;
    }

    public long getTotalPenaltyUnpaid() {
        return totalPenaltyUnpaid;
    }

    public void setTotalPenaltyUnpaid(long totalPenaltyUnpaid) {
        this.totalPenaltyUnpaid = totalPenaltyUnpaid;
    }
}
