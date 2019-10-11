package com.wei.eu.pricing.quartz;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
@DisallowConcurrentExecution
public class CrawlImportJob
                extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger( MethodHandles.lookup().lookupClass() );

    private Job importCrawlFeedJob;

    private JobLauncher jobLauncher;

    @Autowired( required = false )
    public void setJobLauncher( final JobLauncher jobLauncher ) {
        this.jobLauncher = jobLauncher;
    }

    @Autowired( required = false )
    public void setImportCrawlFeedJob( @Qualifier( "importCrawlCsvFileJob" ) final Job importCrawlCsvFileJob ) {
        importCrawlFeedJob = importCrawlCsvFileJob;
    }

    @Override
    protected void executeInternal( @NonNull final JobExecutionContext context )
                    throws JobExecutionException {
        final DateTime endingAt = new DateTime( DateTimeZone.UTC );
        final JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        try {
            if ( importCrawlFeedJob != null ) {
                final JobParameters jobParameters = new JobParametersBuilder()
                                .addString( "JOB_START_DATE", ( new LocalDateTime() ).toString() )
                                .toJobParameters();
                jobLauncher.run( importCrawlFeedJob, jobParameters );
            }
            else {
                final JobKey key = context.getJobDetail().getKey();
                CrawlImportJob.LOGGER.warn( "Job execution for {}.{} skipped due missing job bean.", key.getGroup(), key.getName() );
            }
        }
        catch ( final JobExecutionAlreadyRunningException | JobParametersInvalidException | JobInstanceAlreadyCompleteException | JobRestartException e ) {
            throw new JobExecutionException( e );
        }
    }

}
