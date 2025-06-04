package com.example.tlupickleball.model;

public class Transaction_Club {
    private String title;
    private String description;
    private String date;
    private String amount;

    public Transaction_Club(String title, String description, String date, String amount) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.amount = amount;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getAmount() {
        return amount;
    }
}
