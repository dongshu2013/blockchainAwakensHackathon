package com.walletalarm.platform.jobs;

import com.walletalarm.platform.ApiConfiguration;
import com.walletalarm.platform.db.dao.BlockBatchDAO;
import com.walletalarm.platform.db.dao.TransactionDAO;
import com.walletalarm.platform.handlers.BlockBatchMaintainerJobHandler;
import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.On;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;

@On("0/5 * * * * ?") // Run every 5 seconds
@DisallowConcurrentExecution
public class BlockBatchMaintainerJob extends Job {
    private static final Logger LOGGER = LoggerFactory.getLogger(BlockBatchMaintainerJob.class);

    @Override
    public void doJob(JobExecutionContext context) throws JobExecutionException {
        LOGGER.debug("inside BlockBatchMaintainerJob");
        BlockBatchDAO blockBatchDAO;
        TransactionDAO transactionDAO;
        Web3j web3j;
        SchedulerContext schedulerContext;
        ApiConfiguration apiConfiguration;
        try {
            schedulerContext = context.getScheduler().getContext();
        } catch (SchedulerException e) {
            LOGGER.error("Error getting schedulerContext");
            return;
        }
        apiConfiguration = (ApiConfiguration) schedulerContext.get("ApiConfiguration");
        transactionDAO = (TransactionDAO) schedulerContext.get("TransactionDAO");
        blockBatchDAO = (BlockBatchDAO) schedulerContext.get("BlockBatchDAO");
        web3j = (Web3j) schedulerContext.get("Web3j");
        if (apiConfiguration == null || blockBatchDAO == null || web3j == null) {
            LOGGER.error("One of JobExecutionContext objects is null");
            throw new JobExecutionException("One of JobExecutionContext objects is null");
        }
        BlockBatchMaintainerJobHandler.doBlockBatchMaintenance(blockBatchDAO, transactionDAO, web3j,
                apiConfiguration.getEthMaxBlockNumber(), apiConfiguration.getEthLatestBlockNumber(),
                apiConfiguration.isTestMode());
        LOGGER.debug("BlockBatchMaintainerJob done");
    }
}