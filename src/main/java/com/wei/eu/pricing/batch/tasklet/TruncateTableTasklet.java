package com.wei.eu.pricing.batch.tasklet;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TruncateTableTasklet
                implements Tasklet {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public RepeatStatus execute( @NonNull final StepContribution contribution, @NonNull final ChunkContext chunkContext ) {
        jdbcTemplate.execute( "delete from crawl_data_raw" );
        return RepeatStatus.FINISHED.and( true );
    }

}
