package com.wei.eu.pricing.batch.item;

import com.wei.eu.pricing.model.CrawlExport;
import org.apache.commons.lang.StringUtils;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;

public class CrawlFeedProcessor
                implements ItemProcessor<CrawlExport, CrawlExport> {

    @Override
    public CrawlExport process( @NonNull final CrawlExport item ) {

        if ( StringUtils.isNotBlank( item.getTitle() ) ) {
            if ( item.getTitle().contains( ";" ) ) {
                item.setTitle( item.getTitle().replace( ';', ' ' ) );
            }
            return item;
        }
        else {
            return null;
        }
    }
}
