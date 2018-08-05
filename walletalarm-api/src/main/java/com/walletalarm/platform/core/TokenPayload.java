package com.walletalarm.platform.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenPayload {
    private String address;
    private String payload;
    private String baseCurrency;
    private BlockchainType blockchainType;

    public TokenPayload() {
    }

    public TokenPayload(String address, String payload, String baseCurrency, BlockchainType blockchainType) {
        this.address = address;
        this.payload = payload;
        this.baseCurrency = baseCurrency;
        this.blockchainType = blockchainType;
    }

    @JsonProperty
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @JsonProperty
    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @JsonProperty
    public BlockchainType getBlockchainType() {
        return blockchainType;
    }

    public void setBlockchainType(BlockchainType blockchainType) {
        this.blockchainType = blockchainType;
    }

    @JsonProperty
    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }
}