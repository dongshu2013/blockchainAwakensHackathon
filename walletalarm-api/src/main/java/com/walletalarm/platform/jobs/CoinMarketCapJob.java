package com.walletalarm.platform.jobs;

import com.walletalarm.platform.db.dao.CoinMarketCapDAO;
import com.walletalarm.platform.db.dao.JobDAO;
import com.walletalarm.platform.handlers.CoinMarketCapJobHandler;
import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.On;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
http://www.quartz-scheduler.org/documentation/quartz-2.x/tutorials/tutorial-lesson-06.html
Seconds
Minutes
Hours
Day-of-Month
Month
Day-of-Week
Year (optional field)
 */
//@On("0 * * * * ?") // Run 0th second of every minute - Test
@On("0 30 * * * ?") // Run 0th second 30th minute of every hour - Prod
public class CoinMarketCapJob extends Job {
    private static final Logger LOGGER = LoggerFactory.getLogger(CoinMarketCapJob.class);

    @Override
    public void doJob(JobExecutionContext context) throws JobExecutionException {
        LOGGER.debug("inside CMC job");
        CoinMarketCapDAO coinMarketCapDAO;
        JobDAO jobDAO;
        SchedulerContext schedulerContext;
        try {
            schedulerContext = context.getScheduler().getContext();
        } catch (SchedulerException e) {
            LOGGER.error("Error getting schedulerContext");
            return;
        }
        coinMarketCapDAO = (CoinMarketCapDAO) schedulerContext.get("CoinMarketCapDAO");
        jobDAO = (JobDAO) schedulerContext.get("JobDAO");
        if (coinMarketCapDAO == null || jobDAO == null) {
            LOGGER.error("One of JobExecutionContext objects is null");
            throw new JobExecutionException("One of JobExecutionContext objects is null");
        }
        CoinMarketCapJobHandler.doCoinMarketCapJob(jobDAO, coinMarketCapDAO);
        LOGGER.debug("CMC job done");
    }
}
