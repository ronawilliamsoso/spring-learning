package com.wei.eu.pricing.batch.config;

import com.zoro.eu.domain.price.ExchangeableSalesPrice;
import com.wei.eu.pricing.batch.item.ArticleItemProcessor;
import com.wei.eu.pricing.batch.model.ArticlePriceItem;
import com.wei.eu.pricing.batch.tasklet.CopyFileTasklet;
import com.wei.eu.pricing.batch.tasklet.UpdateArticlePriceItemStateTasklet;
import com.wei.eu.pricing.batch.writer.CsvEscapingLineAggregator;
import com.wei.eu.pricing.batch.writer.CsvItemWriter;
import com.wei.eu.pricing.batch.writer.StringHeaderWriter;
import com.wei.eu.pricing.configuration.PricingProperties;
import com.wei.eu.pricing.model.ArticlePriceState;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ExportPriceFeedBatchConfig {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final PricingProperties pricingProperties;

    @Autowired
    public ExportPriceFeedBatchConfig( final JobBuilderFactory jobBuilderFactory,
                                       final StepBuilderFactory stepBuilderFactory,
                                       final PricingProperties pricingProperties ) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.pricingProperties = pricingProperties;
    }

    @Bean
    public Job createPricingFeed( final Step feedGenerationStep, final JdbcTemplate jdbcTemplate ) {
        return jobBuilderFactory.get( "create-price-feed-job" )
                        .incrementer( new RunIdIncrementer() )
                        .start( getPreExportItemStateUpdateTasklet( jdbcTemplate ) )
                        .next( feedGenerationStep )
                        .next( copyFileStep( pricingProperties.getPriceFeedFileName(),
                                        pricingProperties.getPriceFeedTargetDirectory() ) )
                        .next( getPostExportItemStateUpdateTasklet( jdbcTemplate ) )
                        .build();
    }

    @Bean
    public Step feedGenerationStep( final ItemReader<ArticlePriceItem> priceItemReader ) {
        return stepBuilderFactory.get( "export-price-step" )
                        .<ArticlePriceItem, ExchangeableSalesPrice>chunk( 5000 )
                        .reader( priceItemReader )
                        .processor( new ArticleItemProcessor() )
                        .writer( getCsvItemWriter() )
                        .build();
    }

    private Step copyFileStep( final String sourceFileName, final String targetDirectory ) {
        return stepBuilderFactory.get( "price-copy-file-step" )
                        .tasklet( new CopyFileTasklet( sourceFileName, targetDirectory, true ) )
                        .build();
    }

    private Step getPreExportItemStateUpdateTasklet( final JdbcTemplate jdbcTemplate ) {
        return stepBuilderFactory.get( "mark-dirty-prices-for-processing-step" )
                        .tasklet( new UpdateArticlePriceItemStateTasklet( jdbcTemplate, ArticlePriceState.DIRTY,
                                        ArticlePriceState.PROCESSING ) )
                        .build();
    }

    private Step getPostExportItemStateUpdateTasklet( final JdbcTemplate jdbcTemplate ) {
        return stepBuilderFactory.get( "mark-processed-items-as-synced-step" )
                        .tasklet( new UpdateArticlePriceItemStateTasklet( jdbcTemplate, ArticlePriceState.PROCESSING,
                                        ArticlePriceState.IN_SYNC ) )
                        .build();
    }

    private <T> CsvItemWriter<T> getCsvItemWriter() {
        final CsvItemWriter<T> csvItemWriter = new CsvItemWriter<>( new FlatFileItemWriter<>() );

        final StringHeaderWriter headerWriter = new StringHeaderWriter( pricingProperties.getFileHeaders() );
        csvItemWriter.setHeaderCallback( headerWriter );

        csvItemWriter.setResource( new FileSystemResource( pricingProperties.getPriceFeedFileName() ) );

        final LineAggregator<T> lineAggregator = getLineAggregator( pricingProperties.getExtractorNames() );
        csvItemWriter.setLineAggregator( lineAggregator );
        csvItemWriter.setShouldDeleteIfEmpty( true ); // delete file if empty

        return csvItemWriter;
    }

    private <T> LineAggregator<T> getLineAggregator( final String[] extractorNames ) {
        final CsvEscapingLineAggregator<T> lineAggregator = new CsvEscapingLineAggregator<>();
        lineAggregator.setDelimiter( "," );
        lineAggregator.setFieldExtractor( getFieldExtractor( extractorNames ) );

        return lineAggregator;
    }

    private <T> FieldExtractor<T> getFieldExtractor( final String[] extractorNames ) {
        final BeanWrapperFieldExtractor<T> extractor = new BeanWrapperFieldExtractor<>();
        extractor.setNames( extractorNames );

        return extractor;
    }

    @Bean
    @StepScope
    public ItemReader<ArticlePriceItem> priceItemReader( final DataSource dataSource,
                                                         final MySqlPagingQueryProvider priceExportQueryProvider ) {
        final JdbcPagingItemReader<ArticlePriceItem> databaseReader = new JdbcPagingItemReader<>();

        databaseReader.setSaveState( false );
        databaseReader.setDataSource( dataSource );
        databaseReader.setPageSize( 5000 );
        databaseReader.setRowMapper( new BeanPropertyRowMapper<>( ArticlePriceItem.class ) );
        databaseReader.setQueryProvider( priceExportQueryProvider );

        return databaseReader;
    }

    @Bean
    public MySqlPagingQueryProvider priceExportQueryProvider() {
        final MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();

        queryProvider.setSelectClause( "SELECT *" );
        queryProvider.setFromClause( "FROM article_price a" );
        queryProvider.setWhereClause( "WHERE item_state = " + ArticlePriceState.PROCESSING.ordinal() );

        final Map<String, Order> sortConfiguration = new HashMap<>();
        sortConfiguration.put( "a.article_id", Order.ASCENDING );
        queryProvider.setSortKeys( sortConfiguration );

        return queryProvider;
    }
}
