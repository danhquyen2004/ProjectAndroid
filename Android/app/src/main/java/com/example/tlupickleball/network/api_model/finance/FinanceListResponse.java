package com.example.tlupickleball.network.api_model.finance;

import com.example.tlupickleball.model.logs;

import java.util.List;
public class FinanceListResponse {
    private List<logs> logs;

    public List<logs> getLogs() {
        return logs;
    }

    public void setLogs(List<logs> logs) {
        this.logs = logs;
    }
}
