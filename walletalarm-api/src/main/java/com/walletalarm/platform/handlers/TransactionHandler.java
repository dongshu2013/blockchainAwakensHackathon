package com.walletalarm.platform.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.HttpStatusCodes;
import com.google.common.base.Strings;
import com.walletalarm.platform.cache.ContractCache;
import com.walletalarm.platform.core.*;
import com.walletalarm.platform.db.dao.WalletTransactionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.utils.Convert;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TransactionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionHandler.class);
    private static final String NORMAL_TRANSACTION_FORMAT_URL = "http://api.etherscan.io/api?module=account&action=txlist&" +
            "address=%s&startblock=0&endblock=99999999&sort=asc&apikey=%s";
    private static final String TOKEN_TRANSACTION_FORMAT_URL = "http://api.etherscan.io/api?module=account&action=tokentx&" +
            "address=%s&startblock=0&endblock=999999999&sort=asc&apikey=%s";
    private static final Client CLIENT = ClientBuilder.newClient();

    private static JsonNode getResultJsonNode(String url) throws Exception {
        Response response = CLIENT.target(url)
                .request()
                .get();
        if (response.getStatus() != HttpStatusCodes.STATUS_CODE_OK) {
            LOGGER.error("Error calling Etherscan API - ", response.getStatus());
            throw new Exception("Error calling Etherscan API");
        }
        String jsonString = response.readEntity(String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(jsonString);
        return root.get("result");
    }

    public static void handleSyncTransactions(SyncTransactionInput syncTransactionInput,
                                              WalletTransactionDAO walletTransactionDAO, String apiKey, Web3j web3j) throws Exception {
        List<String> hashList = new ArrayList<>();
        List<BlockchainType> blockchainTypeList = new ArrayList<>();
        List<String> fromList = new ArrayList<>();
        List<String> toList = new ArrayList<>();
        List<TransactionStatus> statusList = new ArrayList<>();
        List<Timestamp> timeList = new ArrayList<>();
        List<BigDecimal> feeList = new ArrayList<>();
        List<BigDecimal> valueList = new ArrayList<>();
        List<String> nameList = new ArrayList<>();
        List<String> symbolList = new ArrayList<>();

        //Get token transactions
        String tokenUrl = String.format(TOKEN_TRANSACTION_FORMAT_URL, syncTransactionInput.getAddress(), apiKey);
        JsonNode resultArrayNode = getResultJsonNode(tokenUrl);
        if (resultArrayNode != null && resultArrayNode.size() > 0) {
            for (int i = 0; i < resultArrayNode.size(); i++) {
                try {
                    JsonNode transactionNode = resultArrayNode.get(i);
                    String hash = transactionNode.get("hash").textValue();
                    Timestamp time = Utils.getTimestampFromEpoch(Long.parseLong(transactionNode.get("timeStamp").textValue()));
                    String to = transactionNode.get("to").textValue();
                    String from = transactionNode.get("from").textValue();
                    String gasPrice = transactionNode.get("gasPrice").textValue();
                    String gasUsed = transactionNode.get("gasUsed").textValue();
                    String input = transactionNode.get("input").textValue();
                    String name;
                    String symbol;
                    BigDecimal value;
                    if (input.startsWith(StringConstants.TRANSFER_METHOD_HASH)) {
                        name = transactionNode.get("tokenName").textValue();
                        symbol = transactionNode.get("tokenSymbol").textValue();
                        String decimalString = transactionNode.get("tokenDecimal").textValue();
                        String valueString = transactionNode.get("value").textValue();
                        value = new BigDecimal(valueString);
                        if (Strings.isNullOrEmpty(name) || Strings.isNullOrEmpty(symbol) || Strings.isNullOrEmpty(decimalString)) {
                            String contractAddress = transactionNode.get("contractAddress").textValue();
                            //Try to fetch from the node
                            Contract contract = ContractCache.getInstance().getContract(contractAddress);
                            if (contract != null) {
                                name = contract.getName();
                                symbol = contract.getSymbol();
                                int decimalInt = contract.getDecimals();
                                value = value.divide(BigDecimal.TEN.pow(decimalInt));
                            }
                        } else {
                            Integer decimals = Integer.parseInt(decimalString);
                            value = value.divide(BigDecimal.TEN.pow(decimals));
                        }
                    } else {
                        name = null;
                        symbol = null;
                        value = BigDecimal.ZERO;
                    }
                    hashList.add(hash);
                    blockchainTypeList.add(BlockchainType.ETH);
                    fromList.add(from);
                    toList.add(to);
                    statusList.add(TransactionStatus.SUCCESS);
                    timeList.add(time);
                    BigDecimal gasPriceInEther = Convert.fromWei(new BigDecimal(gasPrice), Convert.Unit.ETHER);
                    BigDecimal fee = gasPriceInEther.multiply(new BigDecimal(gasUsed));
                    feeList.add(fee);
                    valueList.add(value);
                    nameList.add(Strings.isNullOrEmpty(name) ? StringConstants.UNKNOWN : name);
                    symbolList.add(Strings.isNullOrEmpty(symbol) ? StringConstants.UNKNOWN : symbol);
                } catch (Exception ex) {
                    //Skip item
                    //TODO:take this out
                    System.out.println(ex);
                    System.out.println(ex);
                }
            }
        }

        //Get normal transactions
        String normalUrl = String.format(NORMAL_TRANSACTION_FORMAT_URL, syncTransactionInput.getAddress(), apiKey);
        resultArrayNode = getResultJsonNode(normalUrl);
        if (resultArrayNode != null && resultArrayNode.size() > 0) {
            for (int i = 0; i < resultArrayNode.size(); i++) {
                try {
                    JsonNode transactionNode = resultArrayNode.get(i);
                    String hash = transactionNode.get("hash").textValue();
                    Timestamp time = Utils.getTimestampFromEpoch(Long.parseLong(transactionNode.get("timeStamp").textValue()));
                    String to = transactionNode.get("to").textValue();
                    String from = transactionNode.get("from").textValue();
                    String gasPrice = transactionNode.get("gasPrice").textValue();
                    String gasUsedString = transactionNode.get("gasUsed").textValue();
                    BigDecimal gasUsed = new BigDecimal(gasUsedString);
                    BigDecimal gasPriceInEther = Convert.fromWei(new BigDecimal(gasPrice),
                            Convert.Unit.ETHER);
                    BigDecimal fee = gasPriceInEther.multiply(gasUsed);
                    BigInteger value = new BigInteger(transactionNode.get("value").textValue());
                    String transactionStatusString = transactionNode.get("txreceipt_status").textValue();

                    TransactionStatus status = TransactionStatus.UNKNOWN;
                    if (Strings.isNullOrEmpty(transactionStatusString)) {
                        status = TransactionStatus.UNKNOWN;
                    } else if (transactionStatusString.equals("1")) {
                        status = TransactionStatus.SUCCESS;
                    } else if (transactionStatusString.equals("0")) {
                        status = TransactionStatus.FAIL;
                    }

                    BigDecimal divisor = BigDecimal.TEN.pow(18);
                    BigDecimal realValue = new BigDecimal(value).divide(divisor);

                    hashList.add(hash);
                    blockchainTypeList.add(BlockchainType.ETH);
                    fromList.add(from);
                    toList.add(to);
                    statusList.add(status);
                    timeList.add(time);
                    feeList.add(fee);
                    valueList.add(realValue);
                    nameList.add(StringConstants.ETHEREUM);
                    symbolList.add(StringConstants.ETH);
                } catch (Exception ex) {
                    //Skip item
                    //TODO:take this out
                    System.out.println(ex);
                    System.out.println(ex);
                }
            }
        }

        if (hashList.size() > 0) {
            //Bulk update transactions
            walletTransactionDAO.batchInsert(hashList, blockchainTypeList, fromList, toList, statusList, timeList,
                    feeList, valueList, nameList, symbolList);
        }
    }
}
