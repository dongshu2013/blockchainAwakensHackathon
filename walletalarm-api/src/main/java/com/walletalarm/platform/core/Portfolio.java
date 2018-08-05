package com.walletalarm.platform.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class Portfolio {
    private int portfolioId;
    private String name;
    private int userId;
    private Timestamp createdTime;
    private Timestamp modifiedTime;
    private boolean isActive;

    public Portfolio() {
    }

    public Portfolio(int portfolioId, String name, int userId, Timestamp createdTime, Timestamp modifiedTime, boolean isActive) {
        this.portfolioId = portfolioId;
        this.name = name;
        this.userId = userId;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
        this.isActive = isActive;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    @JsonProperty
    public Timestamp getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Timestamp modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    @JsonProperty
    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    @JsonProperty
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @JsonProperty
    public int getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(int portfolioId) {
        this.portfolioId = portfolioId;
    }
}