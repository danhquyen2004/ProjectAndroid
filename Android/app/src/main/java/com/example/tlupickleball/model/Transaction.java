package com.example.tlupickleball.model;

public class Transaction {
    private String title;
    private String amount;
    private String time;
    private boolean isIncome;

    public Transaction(String title, String amount, String time, boolean isIncome) {
        this.title = title;
        this.amount = amount;
        this.time = time;
        this.isIncome = isIncome;
    }

    public String getTitle() { return title; }
    public String getAmount() { return amount; }
    public String getTime() { return time; }
    public boolean isIncome() { return isIncome; }
}
