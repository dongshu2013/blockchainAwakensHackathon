package com.walletalarm.platform.handlers;

import com.walletalarm.platform.core.BlockchainType;
import com.walletalarm.platform.core.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

import java.math.BigInteger;

public class EthUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(EthUtils.class);
    private static final BigInteger GAS_PRICE = new BigInteger("1");
    private static final BigInteger GAS_LIMIT = new BigInteger("1");
    private static final Credentials CREDENTIALS = Credentials.create(
            "1",  // 32 byte hex value
            "1"  // 64 byte hex value
    );

    public static Contract getContractDetails(Web3j web3j, String contractAddress) {
        Contract contract = new Contract();
        try {
            HumanStandardToken humanStandardToken = new HumanStandardToken(contractAddress, web3j, CREDENTIALS, GAS_PRICE,
                    GAS_LIMIT);
            contract.setAddress(contractAddress);
            contract.setDecimals(humanStandardToken.decimals().send().intValue());
            contract.setName(humanStandardToken.name().send());
            contract.setSymbol(humanStandardToken.symbol().send());
            contract.setBlockchainType(BlockchainType.ETH);
        } catch (Exception ex) {
            LOGGER.error("Error getting contract details for address - " + contractAddress, ex);
            contract = null;
        }
        return contract;
    }
}
