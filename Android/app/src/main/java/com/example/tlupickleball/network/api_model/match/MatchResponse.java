package com.example.tlupickleball.network.api_model.match;

import java.util.List;

import com.example.tlupickleball.model.Match;
import com.google.gson.annotations.SerializedName;

public class MatchResponse {
    @SerializedName("matches")
    private List<Match> matches;
    @SerializedName("page")
    private int page;
    @SerializedName("pageSize")
    private int pageSize;
    @SerializedName("hasNextPage")
    private boolean hasNextPage;

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }
}