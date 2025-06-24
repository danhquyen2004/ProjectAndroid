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

    public String getType() {
        if (amount.startsWith("+")) return "Thu";
        else return "Chi";
    }

    public int getAmountValue() {
        try {
            // Loại bỏ ký tự "+" hoặc "-" và "đ", sau đó parse thành số
            String cleaned = amount.replace("+", "").replace("-", "").replace(".", "").replace("đ", "").trim();
            return Integer.parseInt(cleaned);
        } catch (Exception e) {
            return 0;
        }
    }

    public java.util.Date getDateAsDate() {
        try {
            String[] parts = date.split(" - ");
            return new java.text.SimpleDateFormat("dd/MM/yyyy").parse(parts[1]); // "10/06/2025"
        } catch (Exception e) {
            return new java.util.Date(); // fallback nếu lỗi
        }
    }


}
