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
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

        //Get normal transactions
        String normalUrl = String.format(NORMAL_TRANSACTION_FORMAT_URL, syncTransactionInput.getAddress(), apiKey);
        JsonNode resultArrayNode = getResultJsonNode(normalUrl);
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
                    BigInteger value = new BigInteger(transactionNode.get("value").textValue());
                    String transactionStatusString = transactionNode.get("txreceipt_status").textValue();

                    WalletTransaction walletTransaction = extractTransaction(value, from, to, input, hash, time, gasPrice,
                            gasUsed, transactionStatusString, web3j);
                    if (walletTransaction != null) {
                        hashList.add(walletTransaction.getHash());
                        blockchainTypeList.add(BlockchainType.ETH);
                        fromList.add(walletTransaction.getFrom());
                        toList.add(walletTransaction.getTo());
                        statusList.add(walletTransaction.getStatus());
                        timeList.add(walletTransaction.getTime());
                        feeList.add(walletTransaction.getFee());
                        valueList.add(walletTransaction.getValue());
                        nameList.add(walletTransaction.getName());
                        symbolList.add(walletTransaction.getSymbol());
                    }
                } catch (Exception ex) {
                    //Skip item
                    //TODO:take this out
                    System.out.println(ex);
                    System.out.println(ex);
                }
            }
        }

        //Get token transactions
        String tokenUrl = String.format(TOKEN_TRANSACTION_FORMAT_URL, syncTransactionInput.getAddress(), apiKey);
        resultArrayNode = getResultJsonNode(tokenUrl);
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
                    BigInteger value = new BigInteger(transactionNode.get("value").textValue());
                    String transactionStatusString = transactionNode.get("txreceipt_status").textValue();

                    WalletTransaction walletTransaction = extractTransaction(value, from, to, input, hash, time, gasPrice,
                            gasUsed, transactionStatusString, web3j);
                    if (walletTransaction != null) {
                        hashList.add(walletTransaction.getHash());
                        blockchainTypeList.add(BlockchainType.ETH);
                        fromList.add(walletTransaction.getFrom());
                        toList.add(walletTransaction.getTo());
                        statusList.add(walletTransaction.getStatus());
                        timeList.add(walletTransaction.getTime());
                        feeList.add(walletTransaction.getFee());
                        valueList.add(walletTransaction.getValue());
                        nameList.add(walletTransaction.getName());
                        symbolList.add(walletTransaction.getSymbol());
                    }
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

    private static WalletTransaction extractTransaction(BigInteger value, String from, String to, String input,
                                                        String hash, Timestamp time, String gasPrice, String gasUsedString,
                                                        String transactionStatusString, Web3j web3j) throws IOException {
        TransactionType transactionType = TransactionType.UNSUPPORTED;
        String originalTo = to;
        WalletTransaction walletTransaction = new WalletTransaction();
        walletTransaction.setHash(hash);
        walletTransaction.setBlockchainType(BlockchainType.ETH);
        String name = null;
        String symbol = null;

        //Get to and from addresses
        if (value.equals(BigInteger.ZERO)) { //Non-ETH transfer transaction
            if (input.startsWith(StringConstants.TRANSFER_METHOD_HASH)) { //Token transfer transaction
                to = Numeric.prependHexPrefix(input.substring(34, 74));
                transactionType = TransactionType.TOKEN_TRANSFER;
            }
        } else { //ETH transfer transaction
            transactionType = TransactionType.NATIVE_TRANSFER;
        }

        if (transactionType == TransactionType.UNSUPPORTED) {
            return null;
        }

        BigDecimal realValue;
        BigDecimal divisor = null;

        //Value
        if (transactionType == TransactionType.TOKEN_TRANSFER) {
            value = Numeric.toBigIntNoPrefix(input.substring(74, 138));
            Contract contract = ContractCache.getInstance().getContract(originalTo);
            if (contract != null) {
                divisor = BigDecimal.TEN.pow(contract.getDecimals());
                name = contract.getName();
                symbol = contract.getSymbol();
            } else { //If you can't get contract details then just assume 18 digits
                divisor = BigDecimal.TEN.pow(18);
                name = StringConstants.UNKNOWN;
                symbol = StringConstants.UNKNOWN;
            }
        } else if (transactionType == TransactionType.NATIVE_TRANSFER) {
            divisor = BigDecimal.TEN.pow(18);
            name = StringConstants.ETHEREUM;
            symbol = StringConstants.ETH;
        }
        realValue = new BigDecimal(value).divide(divisor);

        //Gas & Tx status
        BigDecimal gasPriceInEther = Convert.fromWei(new BigDecimal(gasPrice),
                Convert.Unit.ETHER);
        TransactionStatus transactionStatus = TransactionStatus.SUCCESS;
        BigDecimal gasUsed = BigDecimal.ZERO;

        if (Strings.isNullOrEmpty(transactionStatusString)) {
            Optional<TransactionReceipt> txReceipt =
                    web3j.ethGetTransactionReceipt(hash).send().getTransactionReceipt();
            if (txReceipt.isPresent()) {
                TransactionReceipt transactionReceipt = txReceipt.get();
                if(transactionReceipt.getStatus() != null) {
                    BigInteger status = Numeric.decodeQuantity(transactionReceipt.getStatus());
                    if (status.equals(BigInteger.ZERO)) {
                        transactionStatus = TransactionStatus.FAIL;
                    } else if (status.equals(BigInteger.ONE)) {
                        transactionStatus = TransactionStatus.SUCCESS;
                    }
                }
                gasUsed = new BigDecimal(transactionReceipt.getGasUsed());
            }
        } else {
            transactionStatus = transactionStatusString.equals("1") ? TransactionStatus.SUCCESS :
                    TransactionStatus.FAIL;
            gasUsed = new BigDecimal(gasUsedString);
        }
        BigDecimal fee = gasPriceInEther.multiply(gasUsed);

        walletTransaction.setFee(fee);
        walletTransaction.setStatus(transactionStatus);
        walletTransaction.setFrom(from);
        walletTransaction.setTo(to);
        walletTransaction.setTime(time);
        walletTransaction.setValue(realValue);
        walletTransaction.setName(name);
        walletTransaction.setSymbol(symbol);
        return walletTransaction;
    }
}
