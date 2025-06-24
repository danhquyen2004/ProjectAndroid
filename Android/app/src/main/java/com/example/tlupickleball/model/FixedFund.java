package com.example.tlupickleball.model;

import com.google.gson.annotations.SerializedName;

public class FixedFund {
    @SerializedName("amount")
    private int amount;
    @SerializedName("status")
    private String status;

    public FixedFund(int amount, String status) {
        this.amount = amount;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
