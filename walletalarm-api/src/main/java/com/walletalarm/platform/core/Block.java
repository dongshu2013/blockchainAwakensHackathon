package com.walletalarm.platform.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class Block {
    private int blockId;
    private String blockNumber;
    private String status;
    private String server;
    private Timestamp createdTime;
    private Timestamp modifiedTime;

    public Block() {
    }

    public Block(int blockId, String blockNumber, String status, String server, Timestamp createdTime, Timestamp modifiedTime) {
        this.blockId = blockId;
        this.blockNumber = blockNumber;
        this.status = status;
        this.server = server;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
    }

    @JsonProperty
    public String getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }

    @JsonProperty
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty
    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    @JsonProperty
    public int getBlockId() {
        return blockId;
    }

    public void setBlockId(int blockId) {
        this.blockId = blockId;
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
}