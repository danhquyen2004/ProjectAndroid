package com.example.tlupickleball.model;

public class DateItem {
    private String day;
    private String month;
    private boolean isSelected; // Để quản lý trạng thái được chọn

    public DateItem(String day, String month) {
        this.day = day;
        this.month = month;
        this.isSelected = false;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    // Hiển thị dạng "14\ntháng 5"
    public String getFormattedDate() {
        return day + "\n" + month;
    }
}