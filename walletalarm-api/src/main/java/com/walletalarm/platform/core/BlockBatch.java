package com.walletalarm.platform.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class BlockBatch {
    private long blockBatchId;
    private long startBlockNumber;
    private long endBlockNumber;
    private BlockBatchStatus scanStatus;
    private BlockBatchStatus notifyStatus;
    private String scanServer;
    private String notifyServer;
    private Timestamp scanStartTime;
    private Timestamp scanEndTime;
    private Timestamp scanResetTime;
    private Timestamp notifyStartTime;
    private Timestamp notifyEndTime;
    private Timestamp notifyResetTime;
    private int totalTransactions;
    private Timestamp createdTime;
    private Timestamp modifiedTime;

    public BlockBatch() {
    }

    public BlockBatch(long blockBatchId, long startBlockNumber, long endBlockNumber, BlockBatchStatus scanStatus,
                      BlockBatchStatus notifyStatus, String scanServer, String notifyServer, Timestamp scanStartTime,
                      Timestamp scanEndTime, Timestamp scanResetTime, Timestamp notifyStartTime, Timestamp notifyEndTime,
                      Timestamp notifyResetTime, int totalTransactions, Timestamp createdTime, Timestamp modifiedTime) {
        this.blockBatchId = blockBatchId;
        this.startBlockNumber = startBlockNumber;
        this.endBlockNumber = endBlockNumber;
        this.scanStatus = scanStatus;
        this.notifyStatus = notifyStatus;
        this.scanServer = scanServer;
        this.notifyServer = notifyServer;
        this.scanStartTime = scanStartTime;
        this.scanEndTime = scanEndTime;
        this.scanResetTime = scanResetTime;
        this.notifyStartTime = notifyStartTime;
        this.notifyEndTime = notifyEndTime;
        this.notifyResetTime = notifyResetTime;
        this.totalTransactions = totalTransactions;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
    }

    @JsonProperty
    public Timestamp getNotifyStartTime() {
        return notifyStartTime;
    }

    public void setNotifyStartTime(Timestamp notifyStartTime) {
        this.notifyStartTime = notifyStartTime;
    }

    @JsonProperty
    public Timestamp getNotifyEndTime() {
        return notifyEndTime;
    }

    public void setNotifyEndTime(Timestamp notifyEndTime) {
        this.notifyEndTime = notifyEndTime;
    }

    @JsonProperty
    public Timestamp getNotifyResetTime() {
        return notifyResetTime;
    }

    public void setNotifyResetTime(Timestamp notifyResetTime) {
        this.notifyResetTime = notifyResetTime;
    }

    @JsonProperty
    public long getStartBlockNumber() {
        return startBlockNumber;
    }

    public void setStartBlockNumber(long startBlockNumber) {
        this.startBlockNumber = startBlockNumber;
    }

    @JsonProperty
    public long getEndBlockNumber() {
        return endBlockNumber;
    }

    public void setEndBlockNumber(long endBlockNumber) {
        this.endBlockNumber = endBlockNumber;
    }

    @JsonProperty
    public String getScanServer() {
        return scanServer;
    }

    public void setScanServer(String scanServer) {
        this.scanServer = scanServer;
    }

    @JsonProperty
    public Timestamp getScanStartTime() {
        return scanStartTime;
    }

    public void setScanStartTime(Timestamp scanStartTime) {
        this.scanStartTime = scanStartTime;
    }

    @JsonProperty
    public Timestamp getScanEndTime() {
        return scanEndTime;
    }

    public void setScanEndTime(Timestamp scanEndTime) {
        this.scanEndTime = scanEndTime;
    }

    @JsonProperty
    public int getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(int totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    @JsonProperty
    public long getBlockBatchId() {
        return blockBatchId;
    }

    public void setBlockBatchId(long blockBatchId) {
        this.blockBatchId = blockBatchId;
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
    public BlockBatchStatus getScanStatus() {
        return scanStatus;
    }

    public void setScanStatus(BlockBatchStatus scanStatus) {
        this.scanStatus = scanStatus;
    }

    @JsonProperty
    public Timestamp getScanResetTime() {
        return scanResetTime;
    }

    public void setScanResetTime(Timestamp scanResetTime) {
        this.scanResetTime = scanResetTime;
    }

    @JsonProperty
    public BlockBatchStatus getNotifyStatus() {
        return notifyStatus;
    }

    public void setNotifyStatus(BlockBatchStatus notifyStatus) {
        this.notifyStatus = notifyStatus;
    }

    @JsonProperty
    public String getNotifyServer() {
        return notifyServer;
    }

    public void setNotifyServer(String notifyServer) {
        this.notifyServer = notifyServer;
    }
}
