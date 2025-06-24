package com.example.tlupickleball.model;

public class MemberFund {
    private String name;
    private int fund;
    private int penalty;
    private int donate;

    public MemberFund(String name, int fund, int penalty, int donate) {
        this.name = name;
        this.fund = fund;
        this.penalty = penalty;
        this.donate = donate;
    }

    public String getName() { return name; }
    public int getFund() { return fund; }
    public int getPenalty() { return penalty; }
    public int getDonate() { return donate; }
}

