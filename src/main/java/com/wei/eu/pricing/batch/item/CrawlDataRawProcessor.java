package com.wei.eu.pricing.batch.item;

import com.wei.eu.pricing.model.CrawlDataRaw;
import org.apache.commons.lang.StringUtils;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;

public class CrawlDataRawProcessor
                implements ItemProcessor<CrawlDataRaw, CrawlDataRaw> {

    @Override
    public CrawlDataRaw process( @NonNull final CrawlDataRaw item ) {

        if ( StringUtils.isNotBlank( item.getSeller() ) ) {
            return item;
        }
        else {
            return null;
        }
    }
}
