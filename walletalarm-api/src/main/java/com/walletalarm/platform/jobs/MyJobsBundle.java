package com.walletalarm.platform.jobs;

import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.JobConfiguration;
import de.spinscale.dropwizard.jobs.JobManager;
import de.spinscale.dropwizard.jobs.JobsBundle;
import io.dropwizard.setup.Environment;
import org.quartz.Scheduler;

/**
 * The sole purpose of this class is to access protected member of JobsBundle - JobManager
 */
public class MyJobsBundle extends JobsBundle {
    private final Job[] jobs;

    public MyJobsBundle(Job... jobs) {
        super(jobs);
        this.jobs = jobs;
    }

    public JobManager getJobManager() {
        return jobManager;
    }

    public void run(JobConfiguration configuration, Environment environment) {
        this.jobManager = new JobManager(configuration, this.jobs);
        environment.lifecycle().manage(this.jobManager);
    }

    public Scheduler getScheduler() {
        return this.jobManager.getScheduler();
    }
}
