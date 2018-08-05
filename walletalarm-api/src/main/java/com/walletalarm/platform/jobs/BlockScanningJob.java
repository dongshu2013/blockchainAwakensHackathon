package com.walletalarm.platform.jobs;

import com.walletalarm.platform.ApiConfiguration;
import com.walletalarm.platform.db.dao.*;
import com.walletalarm.platform.handlers.BlockBatchScanningJobHandler;
import de.spinscale.dropwizard.jobs.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;

//@On("0/10 * * * * ?") // Run every 10 seconds
//@DisallowConcurrentExecution
public class BlockScanningJob extends Job {
    private static final Logger LOGGER = LoggerFactory.getLogger(BlockScanningJob.class);

    @Override
    public void doJob(JobExecutionContext context) throws JobExecutionException {
        LOGGER.debug("inside BlockScanningJob");
        JobDAO jobDAO;
        SchedulerContext schedulerContext;
        try {
            schedulerContext = context.getScheduler().getContext();
        } catch (SchedulerException e) {
            LOGGER.error("Error getting schedulerContext");
            return;
        }
        ApiConfiguration apiConfiguration = (ApiConfiguration) schedulerContext.get("ApiConfiguration");
        BlockBatchDAO blockBatchDAO = (BlockBatchDAO) schedulerContext.get("BlockBatchDAO");
        WalletDAO walletDAO = (WalletDAO) schedulerContext.get("WalletDAO");
        ContractDAO contractDAO = (ContractDAO) schedulerContext.get("ContractDAO");
        TransactionDAO transactionDAO = (TransactionDAO) schedulerContext.get("TransactionDAO");
        WalletTransactionDAO walletTransactionDAO = (WalletTransactionDAO) schedulerContext.get("WalletTransactionDAO");
        jobDAO = (JobDAO) schedulerContext.get("JobDAO");
        String server = (String) schedulerContext.get("Server");
        Web3j web3j = (Web3j) schedulerContext.get("Web3j");
        if (apiConfiguration == null || apiConfiguration.getFcmToken() == null || blockBatchDAO == null ||
                walletDAO == null || contractDAO == null || jobDAO == null || server == null || transactionDAO == null
                || walletTransactionDAO == null || web3j == null) {
            LOGGER.error("One of JobExecutionContext objects is null");
            throw new JobExecutionException("One of JobExecutionContext objects is null");
        }
        BlockBatchScanningJobHandler.doBlockBatchScanningJob(jobDAO, blockBatchDAO, walletDAO, contractDAO,
                transactionDAO, walletTransactionDAO, server, web3j);
        LOGGER.debug("BlockScanningJob done");
    }
}