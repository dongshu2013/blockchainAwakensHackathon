package com.walletalarm.platform.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class FundDistribution {
    private int fundDistributionId;
    private int icoId;
    private String name;
    private int percentage;
    private Timestamp createdTime;
    private Timestamp modifiedTime;
    private boolean isActive;

    public FundDistribution() {
    }

    public FundDistribution(int fundDistributionId, int icoId, String name, int percentage, Timestamp createdTime,
                            Timestamp modifiedTime, boolean isActive) {
        this.fundDistributionId = fundDistributionId;
        this.icoId = icoId;
        this.name = name;
        this.percentage = percentage;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
        this.isActive = isActive;
    }

    @JsonProperty
    public int getFundDistributionId() {
        return fundDistributionId;
    }

    public void setFundDistributionId(int fundDistributionId) {
        this.fundDistributionId = fundDistributionId;
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
    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    @JsonProperty
    public int getIcoId() {
        return icoId;
    }

    public void setIcoId(int icoId) {
        this.icoId = icoId;
    }
}
