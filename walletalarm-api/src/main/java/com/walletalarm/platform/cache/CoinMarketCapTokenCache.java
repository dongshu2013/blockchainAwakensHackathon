package com.walletalarm.platform.cache;

import com.walletalarm.platform.core.CoinMarketCapToken;

import java.util.HashMap;
import java.util.Map;

public class CoinMarketCapTokenCache {

    private static volatile CoinMarketCapTokenCache INSTANCE = new CoinMarketCapTokenCache();
    private static volatile Map<String /*symbol*/, CoinMarketCapToken> COIN_MARKET_CAP_TOKEN_MAP = new HashMap<>();

    private CoinMarketCapTokenCache() {
    }

    public static CoinMarketCapTokenCache getInstance() {
        return INSTANCE;
    }

    public void assignMap(Map<String /*symbol*/, CoinMarketCapToken> coinMarketCapTokenMap) {
        COIN_MARKET_CAP_TOKEN_MAP = coinMarketCapTokenMap;
    }

    public CoinMarketCapToken getCoinMarketCapToken(String symbol) {
        return COIN_MARKET_CAP_TOKEN_MAP.get(symbol);
    }
}