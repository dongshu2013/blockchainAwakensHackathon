package com.walletalarm.platform.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.walletalarm.platform.core.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

public class ContractCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContractCache.class);

    private static ContractCache contractCache = new ContractCache();

    public static ContractCache getInstance() {
        return contractCache;
    }

    private static LoadingCache<String, Contract> cache;

    public void initialize(CacheManager cacheManager) {
        if (cache == null) {
            cache = CacheBuilder.newBuilder()
                    .maximumSize(5000)
                    .build(new CacheLoader<String, Contract>() {
                        @Override
                        public Contract load(String address) {
                            return cacheManager.getContractFromDBOrNode(address);
                        }
                    });
        }
    }

    public Contract getContract(String address) {
        try {
            return cache.get(address);
        } catch (ExecutionException ex) {
            LOGGER.error("Unable to retrieve contract from cache for address - " + address, ex);
            return null;
        }
    }
}
