package com.example.tlupickleball.model;

public class DateItem {
    private final String displayText;
    private final String dateValue;

    public DateItem(String displayText, String dateValue) {
        this.displayText = displayText;
        this.dateValue = dateValue;
    }

    public String getDisplayText() {
        return displayText;
    }

    public String getDateValue() {
        return dateValue;
    }
}