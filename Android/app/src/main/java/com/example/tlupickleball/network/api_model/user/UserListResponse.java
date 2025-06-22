package com.example.tlupickleball.network.api_model.user;

import com.example.tlupickleball.model.User;

import java.util.List;

public class UserListResponse {
    private List<User> users;
    private String nextPageToken;

    public List<User> getUsers() { return users; }
    public String getNextPageToken() { return nextPageToken; }
}