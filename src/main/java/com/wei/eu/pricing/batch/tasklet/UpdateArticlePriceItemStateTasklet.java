package com.wei.eu.pricing.batch.tasklet;

import com.wei.eu.pricing.model.ArticlePriceState;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;

@RequiredArgsConstructor
public class UpdateArticlePriceItemStateTasklet
                implements Tasklet {

    private final JdbcTemplate jdbcTemplate;

    private final ArticlePriceState itemStateBefore;

    private final ArticlePriceState itemStateAfter;

    @Override
    public RepeatStatus execute( @NonNull final StepContribution contribution, @NonNull final ChunkContext chunkContext ) {
        jdbcTemplate.execute(
                        "UPDATE article_price SET item_state = "
                                        + itemStateAfter.ordinal() + " WHERE item_state = " + itemStateBefore.ordinal() );
        return RepeatStatus.FINISHED.and( true );
    }
}