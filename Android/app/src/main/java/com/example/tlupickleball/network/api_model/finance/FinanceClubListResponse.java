package com.example.tlupickleball.network.api_model.finance;

import com.example.tlupickleball.model.logClub;

import java.util.List;

public class FinanceClubListResponse {
    private List<logClub> logs;

    public FinanceClubListResponse(List<logClub> logs) {
        this.logs = logs;
    }

    public List<logClub> getLogClub() {
        return logs;
    }

    public void setLogClub(List<logClub> logs) {
        this.logs = logs;
    }
}
