package com.wei.eu.pricing.batch.config;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.wei.eu.pricing.batch.item.CrawlDataRawProcessor;
import com.wei.eu.pricing.batch.tasklet.DownloadSftpFileTasklet;
import com.wei.eu.pricing.batch.tasklet.TruncateTableTasklet;
import com.wei.eu.pricing.configuration.JSchUtils;
import com.wei.eu.pricing.configuration.PricingProperties;
import com.wei.eu.pricing.model.CrawlDataRaw;
import com.wei.eu.pricing.repository.CrawlDataRawRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.beans.PropertyEditor;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Configuration
@AllArgsConstructor
@ConditionalOnProperty( value = "com.zoro.eu.pricing.exportEnabled", havingValue = "true" )
public class ImportCrawlFeedBatchConfig {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final PricingProperties pricingProperties;

    private final CrawlDataRawRepository crawlDataRawRepository;

    @Bean( name = "importCrawlCsvFileJob" )
    public Job importCrawlCsvFileJob( final DownloadSftpFileTasklet downloadSftpFileTasklet,
                                      final TruncateTableTasklet truncateTableTasklet ) {
        return jobBuilderFactory
                        .get( "import-crawl-csv-file-Job" )
                        .incrementer( new RunIdIncrementer() )
                        .start( optionalAllsteps( downloadSftpFileTasklet, truncateTableTasklet ) )
                        .end()
                        .build();
    }

    @Bean
    public Flow optionalAllsteps( final DownloadSftpFileTasklet downloadSftpFileTasklet, final TruncateTableTasklet truncateTableTasklet ) {
        final FlowBuilder<Flow> flowBuilder = new FlowBuilder<>( "optional-import-crawl-data-flow" );
        flowBuilder.start( decider() ).on( "NO" ).end()
                        .on( "YES" ).to( allsteps( downloadSftpFileTasklet, truncateTableTasklet ) )
                        .end();
        return flowBuilder.build();
    }

    @Bean
    public Flow allsteps( final DownloadSftpFileTasklet downloadSftpFileTasklet, final TruncateTableTasklet truncateTableTasklet ) {
        final FlowBuilder<Flow> flowBuilder = new FlowBuilder<>( "import-crawl-data-flow" );
        flowBuilder.start( downloadFileStep( downloadSftpFileTasklet ) )
                        .next( truncateTableStep( truncateTableTasklet ) )
                        .next( readCsvStep() )
                        .end();
        return flowBuilder.build();
    }

    @Bean
    public Step downloadFileStep( final DownloadSftpFileTasklet downloadSftpFileTasklet ) {
        return stepBuilderFactory.get( "download-file-step" )
                        .tasklet( downloadSftpFileTasklet )
                        .build();
    }

    @Bean
    public Step truncateTableStep( final TruncateTableTasklet truncateTableTasklet ) {
        return stepBuilderFactory.get( "truncate-table-step" )
                        .tasklet( truncateTableTasklet )
                        .build();
    }

    @Bean
    public Step readCsvStep() {
        return stepBuilderFactory
                        .get( "read-csv-step" )
                        .<CrawlDataRaw, CrawlDataRaw>chunk( 1000 )
                        .reader( fileReader() )
                        .processor( new CrawlDataRawProcessor() )
                        .writer( repositoryWriter() )
                        .faultTolerant()
                        .skip( Exception.class )
                        .skipPolicy( new AlwaysSkipItemSkipPolicy() )
                        .build();
    }

    private FlatFileItemReader<CrawlDataRaw> fileReader() {
        final Map<Class<?>, PropertyEditor> customEditors = new HashMap<>();

        customEditors.put( BigDecimal.class,
                        new CustomNumberEditor( BigDecimal.class, NumberFormat.getInstance( Locale.GERMANY ), true ) );
        customEditors.put( Integer.class,
                        new CustomNumberEditor( Integer.class, NumberFormat.getInstance( Locale.GERMANY ), true ) );
        customEditors.put( Date.class,
                        new CustomDateEditor( new SimpleDateFormat( "yyyy-MM-dd" ), true ) );

        return new FlatFileItemReaderBuilder<CrawlDataRaw>()
                        .name( "ItemReader" )
                        .resource( new FileSystemResource( pricingProperties.getImportCrawLocalFile() ) )
                        .delimited()
                        .delimiter( "\t" )
                        .names( pricingProperties.getImportCrawlCsvHeader() )
                        .linesToSkip( 1 )
                        .strict( true )
                        .distanceLimit( 0 )
                        .targetType( CrawlDataRaw.class )
                        .customEditors( customEditors )
                        .build();
    }

    private ItemWriter<CrawlDataRaw> repositoryWriter() {
        return crawlDataRawRepository::saveAll;

    }

    @Bean
    public JobExecutionDecider decider() {

        return new JobExecutionDecider() {

            @Autowired
            private JSchUtils jSchUtils;

            @Override
            public FlowExecutionStatus decide( final JobExecution jobExecution, final StepExecution stepExecution ) {

                final ChannelSftp sftpChannel;
                try {
                    sftpChannel = jSchUtils.createChannelSftp( pricingProperties.getAimondoSftpServerUsername(),
                                    pricingProperties.getAimondoSftpServerHost(),
                                    pricingProperties.getAimondoSftpServerPort(),
                                    pricingProperties.getAimondoSftpServerPassword()
                    );
                    sftpChannel.lstat( pricingProperties
                                    .getImportCrawlRemoteFile() );// if the remote file doesn't exist, a JSchException generated
                    return new FlowExecutionStatus( "YES" );
                }
                catch ( final JSchException | SftpException e ) {
                    return new FlowExecutionStatus( "NO" );
                }
                finally {
                    jSchUtils.closeAll();

                }
            }
        };
    }

}
