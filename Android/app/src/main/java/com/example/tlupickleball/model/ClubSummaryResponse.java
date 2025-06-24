package com.example.tlupickleball.model;

import com.google.gson.annotations.SerializedName;

public class ClubSummaryResponse {
    @SerializedName("totalIncome")
    private long totalIncome;
    @SerializedName("totalExpense")
    private long totalExpense;

    public long getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(long totalExpense) {
        this.totalExpense = totalExpense;
    }

    public long getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(long totalIncome) {
        this.totalIncome = totalIncome;
    }
}
