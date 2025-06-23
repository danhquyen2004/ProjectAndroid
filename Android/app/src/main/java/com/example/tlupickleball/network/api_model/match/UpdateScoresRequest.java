// File: com.example.tlupickleball.network.api_model.match.UpdateScoresRequest.java
package com.example.tlupickleball.network.api_model.match;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UpdateScoresRequest {

    @SerializedName("setResults")
    private List<CreateMatchRequest.SetResult> setResults;

    public UpdateScoresRequest(List<CreateMatchRequest.SetResult> setResults) {
        this.setResults = setResults;
    }

    public List<CreateMatchRequest.SetResult> getSetResults() {
        return setResults;
    }
}