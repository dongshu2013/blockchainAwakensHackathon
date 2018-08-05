package com.walletalarm.platform.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CoinMarketCapToken {
    private int id;
    private String name;
    private String symbol;

    public CoinMarketCapToken() {
    }

    public CoinMarketCapToken(int id, String name, String symbol) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}