package com.walletalarm.platform.jobs;

import com.walletalarm.platform.ApiConfiguration;
import com.walletalarm.platform.db.dao.BlockBatchDAO;
import com.walletalarm.platform.db.dao.JobDAO;
import com.walletalarm.platform.db.dao.NotificationDAO;
import com.walletalarm.platform.db.dao.TransactionDAO;
import com.walletalarm.platform.handlers.NotificationJobHandler;
import de.spinscale.dropwizard.jobs.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by patankar on 9/19/17.
 */
//@On("0/10 * * * * ?") // Run every 10 seconds
//@DisallowConcurrentExecution
public class NotificationJob extends Job {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationJob.class);

    @Override
    public void doJob(JobExecutionContext context) throws JobExecutionException {
        LOGGER.debug("inside notification job");
        BlockBatchDAO blockBatchDAO;
        TransactionDAO transactionDAO;
        NotificationDAO notificationDAO;
        String server;
        JobDAO jobDAO;
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
        jobDAO = (JobDAO) schedulerContext.get("JobDAO");
        if (apiConfiguration == null || apiConfiguration.getFcmToken() == null || blockBatchDAO == null ||
                notificationDAO == null || jobDAO == null || server == null || transactionDAO == null) {
            LOGGER.error("One of JobExecutionContext objects is null");
            throw new JobExecutionException("One of JobExecutionContext objects is null");
        }
        NotificationJobHandler.doNotificationJob(jobDAO, blockBatchDAO, notificationDAO, transactionDAO, server,
                apiConfiguration.getFcmToken());
        LOGGER.debug("notification job done");
    }
}
