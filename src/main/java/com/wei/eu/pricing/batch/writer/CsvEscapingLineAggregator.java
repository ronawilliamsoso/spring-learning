package com.wei.eu.pricing.batch.writer;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.lang.NonNull;

public class CsvEscapingLineAggregator<T>
                extends DelimitedLineAggregator<T> {

    @Override
    @NonNull
    public String doAggregate( @NonNull final Object[] fields ) {
        for ( int index = 0; index < fields.length; index++ ) {
            if ( fields[index] instanceof String ) {
                fields[index] = StringEscapeUtils.escapeCsv( (String) fields[index] );
            }
        }

        return super.doAggregate( fields );
    }
}
