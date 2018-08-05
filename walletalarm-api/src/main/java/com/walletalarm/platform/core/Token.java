package com.walletalarm.platform.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class Token {
    private String cmcUrl;
    private String name;
    private String symbol;
    private BigDecimal balance;

    public Token() {
    }

    public Token(String cmcUrl, String name, String symbol, BigDecimal balance) {
        this.cmcUrl = cmcUrl;
        this.name = name;
        this.symbol = symbol;
        this.balance = balance;
    }

    @JsonProperty
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @JsonProperty
    public String getCmcUrl() {
        return cmcUrl;
    }

    public void setCmcUrl(String cmcUrl) {
        this.cmcUrl = cmcUrl;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
