package com.walletalarm.platform.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class Wallet {
    private int walletId;
    private String address;
    private String name;
    private BlockchainType blockchainType;
    private int portfolioId;
    private int userId;
    private Timestamp createdTime;
    private Timestamp modifiedTime;
    private boolean isActive;

    public Wallet() {
    }

    public Wallet(int walletId, String address, String name, BlockchainType blockchainType, int portfolioId,
                  int userId, Timestamp createdTime, Timestamp modifiedTime, boolean isActive) {
        this.walletId = walletId;
        this.address = address;
        this.name = name;
        this.blockchainType = blockchainType;
        this.portfolioId = portfolioId;
        this.userId = userId;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
        this.isActive = isActive;
    }

    @JsonProperty
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public BlockchainType getBlockchainType() {
        return blockchainType;
    }

    public void setBlockchainType(BlockchainType blockchainType) {
        this.blockchainType = blockchainType;
    }

    @JsonProperty
    public int getWalletId() {
        return walletId;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
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
