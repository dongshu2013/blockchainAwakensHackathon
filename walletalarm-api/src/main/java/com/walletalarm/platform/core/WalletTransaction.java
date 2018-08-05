package com.walletalarm.platform.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class WalletTransaction {
    private int walletTransactionId;
    private String hash;
    private BlockchainType blockchainType;
    private String from;
    private String to;
    private TransactionStatus status;
    private Timestamp time;
    private BigDecimal fee;
    private BigDecimal value;
    private String name;
    private String symbol;
    private String note;

    public WalletTransaction() {
    }

    public WalletTransaction(int walletTransactionId, String hash, BlockchainType blockchainType, String from, String to,
                             TransactionStatus status, Timestamp time, BigDecimal fee, BigDecimal value, String name,
                             String symbol, String note) {
        this.walletTransactionId = walletTransactionId;
        this.hash = hash;
        this.blockchainType = blockchainType;
        this.from = from;
        this.to = to;
        this.status = status;
        this.time = time;
        this.fee = fee;
        this.value = value;
        this.name = name;
        this.symbol = symbol;
        this.note = note;
    }

    @JsonProperty
    public BlockchainType getBlockchainType() {
        return blockchainType;
    }

    public void setBlockchainType(BlockchainType blockchainType) {
        this.blockchainType = blockchainType;
    }

    @JsonProperty
    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    @JsonProperty
    public int getWalletTransactionId() {
        return walletTransactionId;
    }

    public void setWalletTransactionId(int walletTransactionId) {
        this.walletTransactionId = walletTransactionId;
    }

    @JsonProperty
    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @JsonProperty
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @JsonProperty
    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @JsonProperty
    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    @JsonProperty
    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    @JsonProperty
    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @JsonProperty
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
