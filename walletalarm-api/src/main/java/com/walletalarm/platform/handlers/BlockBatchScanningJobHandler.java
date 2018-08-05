package com.walletalarm.platform.handlers;

import com.google.common.base.Strings;
import com.walletalarm.platform.core.*;
import com.walletalarm.platform.db.dao.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BlockBatchScanningJobHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(BlockBatchScanningJobHandler.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static HashSet<String /*Subscription Address*/> SUBSCRIPTION_ADDRESS_SET = new HashSet<>();
    private static HashMap<String /*Contract Address*/, Contract> CONTRACT_MAP = new HashMap<>();
    private static Timestamp CONTRACTS_CURRENT_MAX_SUBSCRIPTION_MODIFIED_TIME = Timestamp.valueOf("1900-01-01 00:00:00");
    private static Timestamp SUBSCRIPTIONS_CURRENT_MAX_SUBSCRIPTION_MODIFIED_TIME = Timestamp.valueOf("1900-01-01 00:00:00");

    BlockBatchScanningJobHandler() {
        FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static int doBlockBatchScanningJob(JobDAO jobDAO, BlockBatchDAO blockBatchDAO, WalletDAO walletDAO,
                                              ContractDAO contractDAO, TransactionDAO transactionDAO,
                                              WalletTransactionDAO walletTransactionDAO, String server, Web3j web3j) {
        //Create job info
        Job job = new Job();
        job.setStatus(JobStatus.STARTED);
        job.setType(JobType.BLOCK_SCRAPPING);
        int jobId = jobDAO.create(job);

        LOGGER.debug("Starting Block Scrapping Job. Start time - " + DATE_TIME_FORMATTER.format(LocalDateTime.now()));

        scanBlockBatch(blockBatchDAO, walletDAO, contractDAO, transactionDAO, walletTransactionDAO, server, web3j);

        LOGGER.debug("Finished Block Scrapping Job. End time - " + DATE_TIME_FORMATTER.format(LocalDateTime.now()));

        //Update job info
        job.setJobId(jobId);
        job.setStatus(JobStatus.FINISHED);
        jobDAO.update(job);

        return jobId;
    }

    private static void scanBlockBatch(BlockBatchDAO blockBatchDAO, WalletDAO walletDAO, ContractDAO contractDAO,
                                       TransactionDAO transactionDAO, WalletTransactionDAO walletTransactionDAO,
                                       String server, Web3j web3j) {
        BlockBatch blockBatch = blockBatchDAO.getNextScanBlockBatch(server);

        if (blockBatch == null) { //If another server has picked up this batch move on the next available batch
            return;
            //continue;
        }

        //Update in-memory sets of subscription addresses and contract metadata
        //TODO:Use guava caches instead
        updateSubscriptionAddresses(walletDAO);
        updateContracts(contractDAO);

        //Get block one by one
        try {
            //Elements for bulk update
            List<Long> blockBatchIdList = new ArrayList<>();
            List<String> hashList = new ArrayList<>();
            List<BlockchainType> blockchainTypeList = new ArrayList<>();
            List<String> matchingAddressList = new ArrayList<>();
            List<String> fromList = new ArrayList<>();
            List<String> toList = new ArrayList<>();
            List<TransactionStatus> statusList = new ArrayList<>();
            List<Timestamp> timeList = new ArrayList<>();
            List<BigDecimal> feeList = new ArrayList<>();
            List<BigDecimal> valueList = new ArrayList<>();
            List<Integer> contractIdList = new ArrayList<>();
            List<String> nameList = new ArrayList<>();
            List<String> symbolList = new ArrayList<>();
            int totalTransactions = 0;

            for (long i = blockBatch.getStartBlockNumber(); i <= blockBatch.getEndBlockNumber(); i++) {
                //Get Block
                BigInteger blockNumber = BigInteger.valueOf(i);
                EthBlock.Block block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockNumber),
                        true).send().getBlock();
                Timestamp time = Utils.getTimestampFromEpoch(block.getTimestamp().longValue());
                List<EthBlock.TransactionResult> transactions = block.getTransactions();
                totalTransactions += transactions.size();

                //Process all transactions in a block
                for (EthBlock.TransactionResult transactionResult : transactions) {
                    EthBlock.TransactionObject transactionObject = (EthBlock.TransactionObject) transactionResult;

                    BigInteger value = transactionObject.getValue();
                    TransactionType transactionType = TransactionType.UNSUPPORTED;
                    String to = null;
                    String from = transactionObject.getFrom();
                    String input = null;

                    //Get to and from addresses
                    if (value.equals(BigInteger.ZERO)) { //Non-ETH transfer transaction
                        input = transactionObject.getInput();
                        if (input.startsWith(StringConstants.TRANSFER_METHOD_HASH)) { //Token transfer transaction
                            to = Numeric.prependHexPrefix(input.substring(34, 74));
                            transactionType = TransactionType.TOKEN_TRANSFER;
                        }
                    } else { //ETH transfer transaction
                        to = transactionObject.getTo();
                        transactionType = TransactionType.NATIVE_TRANSFER;
                    }

                    //Only continue further if these addresses are of interest and transaction is supported
                    String matchingAddress = null;
                    boolean toMatches;
                    if (to != null) {
                        toMatches = SUBSCRIPTION_ADDRESS_SET.contains(to);
                        if (toMatches) {
                            matchingAddress = to;
                        }
                    }
                    boolean fromMatches = SUBSCRIPTION_ADDRESS_SET.contains(from);
                    if (fromMatches) {
                        matchingAddress = from;
                    }

                    if (!Strings.isNullOrEmpty(matchingAddress)) { //Matching address found
                        if (transactionType == TransactionType.UNSUPPORTED) {
                            continue;
                        }

                        BigDecimal realValue;
                        BigDecimal divisor = null;
                        int contractId = 0;
                        String name = null;
                        String symbol = null;

                        //Value
                        if (transactionType == TransactionType.TOKEN_TRANSFER) {
                            value = Numeric.toBigIntNoPrefix(input.substring(74, 138));
                            String contractAddress = transactionObject.getTo();

                            Contract contract = CONTRACT_MAP.get(contractAddress);
                            if (contract == null) { //New contract
                                //Fetch from Db
                                contract = contractDAO.findByAddress(contractAddress);
                                if (contract == null) { //Contract absent in db
                                    //Fetch from node
                                    contract = EthUtils.getContractDetails(web3j, contractAddress);
                                    if (contract != null) {
                                        contractId = contractDAO.create(contract);
                                        contract.setContractId(contractId);
                                    }
                                }
                                if (contract != null) {
                                    CONTRACT_MAP.put(contractAddress, contract);
                                }
                            }
                            if (contract != null) {
                                contractId = contract.getContractId();
                                name = contract.getName();
                                symbol = contract.getSymbol();
                                divisor = BigDecimal.TEN.pow(contract.getDecimals());
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
                        BigDecimal gasPriceInEther = Convert.fromWei(new BigDecimal(transactionObject.getGasPrice()),
                                Convert.Unit.ETHER);
                        TransactionStatus transactionStatus = TransactionStatus.SUCCESS;
                        BigDecimal gasUsed = BigDecimal.ZERO;

                        Optional<TransactionReceipt> txReceipt =
                                web3j.ethGetTransactionReceipt(transactionObject.getHash()).send().getTransactionReceipt();
                        if (txReceipt.isPresent()) {
                            TransactionReceipt transactionReceipt = txReceipt.get();
                            BigInteger status = Numeric.decodeQuantity(transactionReceipt.getStatus());
                            if (status.equals(BigInteger.ZERO)) {
                                transactionStatus = TransactionStatus.FAIL;
                            } else if (status.equals(BigInteger.ONE)) {
                                transactionStatus = TransactionStatus.SUCCESS;
                            }
                            gasUsed = new BigDecimal(transactionReceipt.getGasUsed());
                        }
                        BigDecimal fee = gasPriceInEther.multiply(gasUsed);

                        blockBatchIdList.add(blockBatch.getBlockBatchId());
                        hashList.add(transactionObject.getHash());
                        blockchainTypeList.add(BlockchainType.ETH);
                        feeList.add(fee);
                        statusList.add(transactionStatus);
                        matchingAddressList.add(matchingAddress);
                        fromList.add(from);
                        toList.add(to);
                        timeList.add(time);
                        valueList.add(realValue);
                        contractIdList.add(contractId == 0 ? null : contractId);
                        nameList.add(name);
                        symbolList.add(symbol);
                    }
                }
            }
            if (hashList.size() > 0) {
                //Bulk insert transactions
                transactionDAO.batchInsert(blockBatchIdList, hashList, blockchainTypeList, matchingAddressList,
                        fromList, toList, statusList, timeList, feeList, valueList, contractIdList, nameList, symbolList);

                //Bulk insert transactions
                walletTransactionDAO.batchInsert(hashList, blockchainTypeList, fromList, toList, statusList, timeList,
                        feeList, valueList, nameList, symbolList);
            }

            //Commit BlockBatch entry
            blockBatchDAO.commitBlockBatchScan(totalTransactions, blockBatch.getBlockBatchId(), server);
        } catch (Exception ex) {
            LOGGER.error("Error scrapping BlockBatch, ", ex);
        }
    }

    private static void updateSubscriptionAddresses(WalletDAO walletDAO) {
        Timestamp latestSubscriptionModifiedTime = walletDAO.getLatestModifiedTime();
        Iterator<String> addressIterator = walletDAO.getNewAddressesAdded(SUBSCRIPTIONS_CURRENT_MAX_SUBSCRIPTION_MODIFIED_TIME,
                latestSubscriptionModifiedTime);
        while (addressIterator.hasNext()) {
            String newAddress = addressIterator.next();
            SUBSCRIPTION_ADDRESS_SET.add(newAddress);
        }
        addressIterator = walletDAO.getNewAddressesRemoved(SUBSCRIPTIONS_CURRENT_MAX_SUBSCRIPTION_MODIFIED_TIME,
                latestSubscriptionModifiedTime);
        while (addressIterator.hasNext()) {
            String removedAddress = addressIterator.next();
            SUBSCRIPTION_ADDRESS_SET.remove(removedAddress);
        }
        SUBSCRIPTIONS_CURRENT_MAX_SUBSCRIPTION_MODIFIED_TIME = latestSubscriptionModifiedTime;
    }

    private static void updateContracts(ContractDAO contractDAO) {
        Timestamp latestContractModifiedTime = contractDAO.getLatestModifiedTime();
        List<Contract> contractList = contractDAO.getNewContractsAdded(CONTRACTS_CURRENT_MAX_SUBSCRIPTION_MODIFIED_TIME,
                latestContractModifiedTime);
        for (Contract contract : contractList) {
            CONTRACT_MAP.put(contract.getAddress(), contract);
        }
        CONTRACTS_CURRENT_MAX_SUBSCRIPTION_MODIFIED_TIME = latestContractModifiedTime;
    }

}