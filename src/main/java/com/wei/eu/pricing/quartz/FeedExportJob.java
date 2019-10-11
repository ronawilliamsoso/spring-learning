package com.wei.eu.pricing.quartz;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
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
import java.util.Date;

@Component
@DisallowConcurrentExecution
public class FeedExportJob
                extends QuartzJobBean {

    private static final String PARAM_JOB_START_DATE = "jobStartDate";

    private static final Logger LOGGER = LoggerFactory.getLogger( MethodHandles.lookup().lookupClass() );

    private JobLauncher jobLauncher;

    private Job priceFeedJob;

    @Autowired( required = false )
    public void setJobLauncher( final JobLauncher jobLauncher ) {
        this.jobLauncher = jobLauncher;
    }

    @Autowired( required = false )
    public void setPriceFeedJob( @Qualifier( "createPricingFeed" ) final Job priceFeedJob ) {
        this.priceFeedJob = priceFeedJob;
    }

    @Override
    protected void executeInternal( @NonNull final JobExecutionContext context )
                    throws JobExecutionException {
        try {
            if ( priceFeedJob != null ) {
                final JobExecution execution = jobLauncher.run( priceFeedJob, getJobParameters() );

                if ( !ExitStatus.COMPLETED.equals( execution.getExitStatus() ) ) {
                    throw new JobExecutionException( "Job execution for 'ExportPriceFeed' exit status was: " + execution.getExitStatus(),
                                    true );
                }
            }
            else {
                final JobKey key = context.getJobDetail().getKey();
                FeedExportJob.LOGGER.warn( "Job execution for {}.{} skipped due missing job bean.", key.getGroup(), key.getName() );
            }
        }
        catch ( final JobExecutionAlreadyRunningException | JobParametersInvalidException | JobInstanceAlreadyCompleteException | JobRestartException e ) {
            throw new JobExecutionException( e, true );
        }
    }

    private JobParameters getJobParameters() {
        return new JobParametersBuilder()
                        .addDate( FeedExportJob.PARAM_JOB_START_DATE, new Date() )
                        .toJobParameters();
    }
}
