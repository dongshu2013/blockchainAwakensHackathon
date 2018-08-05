package com.walletalarm.platform.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SyncTransactionInput {
    private String address;
    private BlockchainType blockchainType;

    public SyncTransactionInput() {
    }

    public SyncTransactionInput(String address, BlockchainType blockchainType) {
        this.address = address;
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
    public BlockchainType getBlockchainType() {
        return blockchainType;
    }

    public void setBlockchainType(BlockchainType blockchainType) {
        this.blockchainType = blockchainType;
    }
}