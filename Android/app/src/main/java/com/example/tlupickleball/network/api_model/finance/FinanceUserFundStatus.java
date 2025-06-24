package com.example.tlupickleball.network.api_model.finance;

import com.example.tlupickleball.model.FixedFund;
import com.example.tlupickleball.model.FundStatusAll;
import com.example.tlupickleball.model.Penalty;

import java.util.List;

public class FinanceUserFundStatus {
    private List<FundStatusAll> results;

    public List<FundStatusAll> getResults() {
        return results;
    }

    public void setResults(List<FundStatusAll> results) {
        this.results = results;
    }
}
