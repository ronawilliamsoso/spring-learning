package com.wei.eu.pricing.batch.config;

import com.wei.eu.pricing.batch.item.CrawlFeedProcessor;
import com.wei.eu.pricing.batch.tasklet.UploadSftpFileTasklet;
import com.wei.eu.pricing.batch.writer.CsvEscapingLineAggregator;
import com.wei.eu.pricing.batch.writer.CsvItemWriter;
import com.wei.eu.pricing.batch.writer.StringHeaderWriter;
import com.wei.eu.pricing.configuration.PricingProperties;
import com.wei.eu.pricing.model.CrawlExport;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@AllArgsConstructor
@ConditionalOnProperty( value = "com.zoro.eu.pricing.exportEnabled", havingValue = "true" )
public class ExportCrawlFeedBatchConfig {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final PricingProperties pricingProperties;

    @Bean( name = "exportCrawlCsvFileJob" )
    public Job exportCrawlCsvFileJob( final Step exportCrawlFeedGenerationStep, final UploadSftpFileTasklet uploadSftpFileTasklet ) {
        return jobBuilderFactory
                        .get( "export-crawl-csv-file-Job" )
                        .incrementer( new RunIdIncrementer() )
                        .start( exportCrawlFeedGenerationStep )
                        .next( uploadFileStep( uploadSftpFileTasklet ) )
                        .build();
    }

    @Bean
    public Step exportCrawlFeedGenerationStep( final ItemReader<CrawlExport> crawlItemReader ) {
        return stepBuilderFactory.get( "crawl-feed-generation-step" )
                        .<CrawlExport, CrawlExport>chunk( 5000 )
                        .reader( crawlItemReader )
                        .processor( new CrawlFeedProcessor() )
                        .writer( getCsvItemWriter() )
                        .build();
    }

    @Bean
    public Step uploadFileStep( final UploadSftpFileTasklet uploadSftpFileTasklet ) {
        return stepBuilderFactory.get( "upload-crawl-file-step" )
                        .tasklet( uploadSftpFileTasklet )
                        .build();
    }

    @Bean
    public UploadSftpFileTasklet uploadSftpFileTasklet() {
        return new UploadSftpFileTasklet();
    }

    @Bean
    @StepScope
    public ItemReader<CrawlExport> crawlItemReader( final DataSource dataSource,
                                                    final MySqlPagingQueryProvider crawlExportQueryProvider ) {
        final JdbcPagingItemReader<CrawlExport> databaseReader = new JdbcPagingItemReader<>();

        databaseReader.setSaveState( false );
        databaseReader.setDataSource( dataSource );
        databaseReader.setPageSize( 5000 );
        databaseReader.setRowMapper( new BeanPropertyRowMapper<>( CrawlExport.class ) );
        databaseReader.setQueryProvider( crawlExportQueryProvider );

        return databaseReader;
    }

    @Bean
    public MySqlPagingQueryProvider crawlExportQueryProvider() {
        final MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause( "SELECT *" );
        queryProvider.setFromClause( "FROM Crawl_15000" );

        final Map<String, Order> sortConfiguration = new HashMap<>();
        sortConfiguration.put( "ops", Order.DESCENDING );
        sortConfiguration.put( "product_id", Order.DESCENDING );
        queryProvider.setSortKeys( sortConfiguration );
        return queryProvider;
    }

    private <T> CsvItemWriter<T> getCsvItemWriter() {
        final CsvItemWriter<T> csvItemWriter = new CsvItemWriter<>( new FlatFileItemWriter<>() );

        final StringHeaderWriter headerWriter = new StringHeaderWriter( pricingProperties.getExportCrawlCsvHeader() );
        csvItemWriter.setHeaderCallback( headerWriter );

        csvItemWriter.setResource( new FileSystemResource( pricingProperties.getExportCrawlCsvFile() ) );

        final LineAggregator<T> lineAggregator = getLineAggregator( pricingProperties.getExportCrawlCsvExtractorNames() );
        csvItemWriter.setLineAggregator( lineAggregator );
        csvItemWriter.setShouldDeleteIfEmpty( true ); // delete file if empty
        return csvItemWriter;
    }

    private <T> LineAggregator<T> getLineAggregator( final String[] extractorNames ) {
        final CsvEscapingLineAggregator<T> lineAggregator = new CsvEscapingLineAggregator<>();
        lineAggregator.setDelimiter( ";" );
        lineAggregator.setFieldExtractor( getFieldExtractor( extractorNames ) );

        return lineAggregator;
    }

    private <T> FieldExtractor<T> getFieldExtractor( final String[] extractorNames ) {
        final BeanWrapperFieldExtractor<T> extractor = new BeanWrapperFieldExtractor<>();
        extractor.setNames( extractorNames );

        return extractor;
    }

}
