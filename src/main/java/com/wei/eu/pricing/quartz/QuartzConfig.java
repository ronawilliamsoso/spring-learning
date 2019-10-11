package com.wei.eu.pricing.quartz;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.spi.JobFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

import java.util.TimeZone;

@Configuration
public class QuartzConfig {

    @Bean
    public JobFactory jobFactory( final ApplicationContext applicationContext ) {
        final AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext( applicationContext );
        return jobFactory;
    }

    @Bean
    public JobDetailFactoryBean priceFeedJobDetail() {
        final JobDetailFactoryBean factory = new JobDetailFactoryBean();
        factory.setJobClass( FeedExportJob.class );
        factory.setName( "price-feed-export-job" );
        factory.setGroup( "price-export" );
        factory.setDescription( "Generates price feeds" );
        factory.setDurability( true );

        return factory;
    }

    @Bean
    public CronTriggerFactoryBean priceFeedTriggerFactoryBean( final JobDetail priceFeedJobDetail ) {
        final CronTriggerFactoryBean cronTrigger = new CronTriggerFactoryBean();
        cronTrigger.setName( "price-feed-export-cron-trigger" );
        cronTrigger.setGroup( "price-export" );
        cronTrigger.setJobDetail( priceFeedJobDetail );
        cronTrigger.setMisfireInstruction( CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW );
        cronTrigger.setCronExpression( "0 0/10 * ? * * *" );
        cronTrigger.setTimeZone( TimeZone.getTimeZone( "Europe/Berlin" ) );
        cronTrigger.setPriority( 5 );

        return cronTrigger;
    }

    @Bean
    public JobDetailFactoryBean CrowDataImportJobDetail() {
        final JobDetailFactoryBean factory = new JobDetailFactoryBean();
        factory.setJobClass( CrawlImportJob.class );
        factory.setName( "crawl-feed-import-job" );
        factory.setGroup( "crawl-import" );
        factory.setDescription( "import crawl feeds" );
        factory.setDurability( true );

        return factory;
    }

    @Bean
    public CronTriggerFactoryBean CrowDataImportTriggerFactoryBean( final JobDetail CrowDataImportJobDetail ) {
        final CronTriggerFactoryBean cronTrigger = new CronTriggerFactoryBean();
        cronTrigger.setName( "crawl-feed-import-cron-trigger" );
        cronTrigger.setGroup( "crawl-import" );
        cronTrigger.setJobDetail( CrowDataImportJobDetail );
        cronTrigger.setMisfireInstruction( CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW );
        cronTrigger.setCronExpression( "0 0 * ? * * *" );
        cronTrigger.setTimeZone( TimeZone.getTimeZone( "Europe/Berlin" ) );
        cronTrigger.setPriority( 5 );

        return cronTrigger;
    }

    @Bean
    public JobDetailFactoryBean CrowDataExportJobDetail() {
        final JobDetailFactoryBean factory = new JobDetailFactoryBean();
        factory.setJobClass( CrawlExportJob.class );
        factory.setName( "crawl-feed-export-job" );
        factory.setGroup( "crawl-export" );
        factory.setDescription( "export crawl feeds" );
        factory.setDurability( true );

        return factory;
    }

    @Bean
    public CronTriggerFactoryBean CrowDataExportTriggerFactoryBean( final JobDetail CrowDataExportJobDetail ) {
        final CronTriggerFactoryBean cronTrigger = new CronTriggerFactoryBean();
        cronTrigger.setName( "crawl-feed-export-cron-trigger" );
        cronTrigger.setGroup( "crawl-export" );
        cronTrigger.setJobDetail( CrowDataExportJobDetail );
        cronTrigger.setMisfireInstruction( CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW );
        cronTrigger.setCronExpression( "0 0 19 * * ?" );// every day at 19
        cronTrigger.setTimeZone( TimeZone.getTimeZone( "Europe/Berlin" ) );
        cronTrigger.setPriority( 5 );

        return cronTrigger;
    }

}
