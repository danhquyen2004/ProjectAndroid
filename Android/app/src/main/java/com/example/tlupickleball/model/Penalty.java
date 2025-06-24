package com.example.tlupickleball.model;

import com.google.gson.annotations.SerializedName;

public class Penalty {
    @SerializedName("total")
    private long total;
    @SerializedName("paid")
    private long paid;
    @SerializedName("unpaid")
    private long unpaid;
    @SerializedName("status")
    private String status;

    public Penalty(long total, long paid, long unpaid, String status) {
        this.total = total;
        this.paid = paid;
        this.unpaid = unpaid;
        this.status = status;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getPaid() {
        return paid;
    }

    public void setPaid(long paid) {
        this.paid = paid;
    }

    public long getUnpaid() {
        return unpaid;
    }

    public void setUnpaid(long unpaid) {
        this.unpaid = unpaid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
