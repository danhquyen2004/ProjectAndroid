package com.example.tlupickleball.model;

public class Transaction {
    private String title;
    private String amount;
    private String time;
    private String status;
    private boolean isIncome;

    public Transaction(String title, String amount, String time, String status, boolean isIncome) {
        this.title = title;
        this.amount = amount;
        this.time = time;
        this.status = status;
        this.isIncome = isIncome;
    }

    public String getTitle() { return title; }
    public String getAmount() { return amount; }
    public String getTime() { return time; }
    public String getStatus() { return status; }
    public boolean isIncome() { return isIncome; }
}
