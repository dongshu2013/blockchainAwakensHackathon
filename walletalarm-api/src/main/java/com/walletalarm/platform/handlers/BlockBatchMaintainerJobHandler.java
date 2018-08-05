package com.walletalarm.platform.handlers;

import com.walletalarm.platform.db.dao.BlockBatchDAO;
import com.walletalarm.platform.db.dao.TransactionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class BlockBatchMaintainerJobHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(BlockBatchMaintainerJobHandler.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final int BLOCK_BATCH_SIZE = 10;

    BlockBatchMaintainerJobHandler() {
        FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static void doBlockBatchMaintenance(BlockBatchDAO blockBatchDAO, TransactionDAO transactionDAO,
                                               Web3j web3j, long configMaxBlockNumber, long configLatestBlockNumber,
                                               boolean configTestMode) {
        LOGGER.debug("Starting BLOCK_BATCH_MAINTAINER Job. Start time - " + DATE_TIME_FORMATTER.format(LocalDateTime.now()));

        doBlockBatchScheduling(blockBatchDAO, transactionDAO, web3j, configMaxBlockNumber, configLatestBlockNumber,
                configTestMode);

        LOGGER.debug("Finished BLOCK_BATCH_MAINTAINER Job. End time - " + DATE_TIME_FORMATTER.format(LocalDateTime.now()));
    }

    private static void doBlockBatchScheduling(BlockBatchDAO blockBatchDAO, TransactionDAO transactionDAO, Web3j web3j,
                                               long configMaxBlockNumber, long configLatestBlockNumber,
                                               boolean configTestMode) {
        long latestBlockNumber = configLatestBlockNumber;
        long maxBlockNumber = configMaxBlockNumber;

        try {
            if (!configTestMode) {
                //Get latest block number
                latestBlockNumber = web3j.ethBlockNumber().send().getBlockNumber().longValueExact();

                //Get max block number from block_batch table
                maxBlockNumber = blockBatchDAO.getMaxBlockNumber();
            }

            LOGGER.debug(String.format("latestBlockNumber - %s, maxBlockNumber - %s", latestBlockNumber, maxBlockNumber));

            //Issue update to this block number
            long diff = latestBlockNumber - maxBlockNumber;
            long startBlockNumber;
            long endBlockNumber = maxBlockNumber;
            long thisBatchSize;
            List<Long> startBlockNumberList = new ArrayList<>();
            List<Long> endBlockNumberList = new ArrayList<>();

            while (diff > 0) {
                startBlockNumber = endBlockNumber + 1;
                thisBatchSize = Math.min(BLOCK_BATCH_SIZE, diff);
                endBlockNumber = endBlockNumber + thisBatchSize;

                //Add to list
                startBlockNumberList.add(startBlockNumber);
                endBlockNumberList.add(endBlockNumber);

                diff = diff - thisBatchSize;
            }

            //Reset maxBlockNumber
            //maxBlockNumber = endBlockNumber;

            if (startBlockNumberList.size() > 0) {
                blockBatchDAO.batchInsert(startBlockNumberList, endBlockNumberList);
            }

            //Run Cleanup Routine
            doBlockCleanUp(blockBatchDAO, transactionDAO);
        } catch (Exception ex) {
            LOGGER.error("Error in doBlockBatchScheduling Job", ex);
        }
    }

    private static void doBlockCleanUp(BlockBatchDAO blockBatchDAO, TransactionDAO transactionDAO) {
        try {
            //Check if older (greater than 2 minutes ago) block batch is still processing and open them up again for reprocessing

            //Scan Blocks
            List<Long> resettedBlockBatchIdList = new ArrayList<>();
            List<Long> blockBatchIdList = blockBatchDAO.getResetScanUnprocessedBlockBatchIds();
            for (long blockBatchId : blockBatchIdList) {
                int rowsUpdatedScan = blockBatchDAO.resetScanUnprocessedJobs(blockBatchId);
                //Reset batches
                if (rowsUpdatedScan == 1) {
                    resettedBlockBatchIdList.add(blockBatchId);
                }
            }
            for (long resettedBlockBatchId : resettedBlockBatchIdList) {
                //Delete transactions from resetted batches
                transactionDAO.deleteByBlockBatchId(resettedBlockBatchId);
            }

            //Notify Blocks
            resettedBlockBatchIdList = new ArrayList<>();
            blockBatchIdList = blockBatchDAO.getResetNotifyUnprocessedBlockBatchIds();
            for (long blockBatchId : blockBatchIdList) {
                int rowsUpdatedNotify = blockBatchDAO.resetNotifyUnprocessedJobs(blockBatchId);
                //Reset batches
                if (rowsUpdatedNotify == 1) {
                    resettedBlockBatchIdList.add(blockBatchId);
                }
            }
            for (long resettedBlockBatchId : resettedBlockBatchIdList) {
                //Delete transactions from resetted batches
                transactionDAO.deleteByBlockBatchId(resettedBlockBatchId);
            }
        } catch (Exception ex) {
            LOGGER.error("Error in doBlockBatchScheduling Job", ex);
        }
    }
}