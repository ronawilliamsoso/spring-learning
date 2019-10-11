package com.wei.eu.pricing.batch.writer;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.ResourceAwareItemWriterItemStream;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.batch.item.support.AbstractItemStreamItemWriter;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;

import java.util.List;

@StepScope
public class CsvItemWriter<T>
                extends AbstractItemStreamItemWriter<T>
                implements ResourceAwareItemWriterItemStream<T> {

    private final FlatFileItemWriter<T> wrapped;

    public CsvItemWriter( final FlatFileItemWriter<T> wrapped ) {
        this.wrapped = wrapped;
    }

    @Override
    public synchronized void write( @NonNull final List<? extends T> list )
                    throws Exception {
        wrapped.write( list );
    }

    @Override
    public void open( @NonNull final ExecutionContext executionContext )
                    throws ItemStreamException {
        wrapped.open( executionContext );
    }

    @Override
    public void close()
                    throws ItemStreamException {
        wrapped.close();
    }

    public void setLineAggregator( final LineAggregator<T> lineAggregator ) {
        wrapped.setLineAggregator( lineAggregator );
    }

    public void setHeaderCallback( final StringHeaderWriter headerWriter ) {
        wrapped.setHeaderCallback( headerWriter );
    }

    public void setShouldDeleteIfEmpty( final boolean shouldDeleteIfEmpty ) {
        wrapped.setShouldDeleteIfEmpty( shouldDeleteIfEmpty );
    }

    @Override
    public void setResource( @NonNull final Resource resource ) {
        wrapped.setResource( resource );
    }

}
