package com.walletalarm.platform.jobs;

import com.walletalarm.platform.ApiConfiguration;
import com.walletalarm.platform.db.dao.BlockBatchDAO;
import com.walletalarm.platform.db.dao.NotificationDAO;
import com.walletalarm.platform.db.dao.TransactionDAO;
import com.walletalarm.platform.handlers.NotificationJobHandler;
import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.On;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@On("0/5 * * * * ?") // Run every 5 seconds
@DisallowConcurrentExecution
public class NotificationJob extends Job {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationJob.class);

    @Override
    public void doJob(JobExecutionContext context) throws JobExecutionException {
        LOGGER.debug("inside notification job");
        BlockBatchDAO blockBatchDAO;
        TransactionDAO transactionDAO;
        NotificationDAO notificationDAO;
        String server;
        SchedulerContext schedulerContext;
        try {
            schedulerContext = context.getScheduler().getContext();
        } catch (SchedulerException e) {
            LOGGER.error("Error getting schedulerContext");
            return;
        }
        ApiConfiguration apiConfiguration = (ApiConfiguration) schedulerContext.get("ApiConfiguration");
        blockBatchDAO = (BlockBatchDAO) schedulerContext.get("BlockBatchDAO");
        transactionDAO = (TransactionDAO) schedulerContext.get("TransactionDAO");
        server = (String) schedulerContext.get("Server");
        notificationDAO = (NotificationDAO) schedulerContext.get("NotificationDAO");
        if (apiConfiguration == null || apiConfiguration.getFcmToken() == null || blockBatchDAO == null ||
                notificationDAO == null || server == null || transactionDAO == null) {
            LOGGER.error("One of JobExecutionContext objects is null");
            throw new JobExecutionException("One of JobExecutionContext objects is null");
        }
        NotificationJobHandler.doNotificationJob(blockBatchDAO, notificationDAO, transactionDAO, server,
                apiConfiguration.getFcmToken());
        LOGGER.debug("notification job done");
    }
}
