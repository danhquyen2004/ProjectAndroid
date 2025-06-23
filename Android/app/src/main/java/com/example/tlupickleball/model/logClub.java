package com.example.tlupickleball.model;

import com.google.gson.annotations.SerializedName;

public class logClub {
    @SerializedName("expenseId")
    private String expenseId;
    @SerializedName("reason")
    private String reason;
    @SerializedName("amount")
    private long amount;
    @SerializedName("createdBy")
    private String createdBy;
    @SerializedName("createdAt")
    private String createdAt;

    public logClub() {
        // Default constructor
    }

    public logClub(String expenseId, String reason, long amount, String createdBy, String createdAt) {
        this.expenseId = expenseId;
        this.reason = reason;
        this.amount = amount;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
