package com.example.tlupickleball.model;
public class DateItem {
    private String dayOfMonth;
    private String month;
    private boolean isSelected;

    public DateItem(String dayOfMonth, String month) {
        this.dayOfMonth = dayOfMonth;
        this.month = month;
        this.isSelected = false; // Mặc định là không được chọn
    }

    // Getters
    public String getDayOfMonth() {
        return dayOfMonth;
    }

    public String getMonth() {
        return month;
    }

    public boolean isSelected() {
        return isSelected;
    }

    // Setters
    public void setDayOfMonth(String dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}