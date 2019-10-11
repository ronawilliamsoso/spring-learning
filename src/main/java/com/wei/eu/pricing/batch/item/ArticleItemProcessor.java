package com.wei.eu.pricing.batch.item;

import com.wei.eu.pricing.batch.model.ArticlePriceItem;
import com.zoro.eu.domain.price.ExchangeableSalesPrice;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;

public class ArticleItemProcessor
                implements ItemProcessor<ArticlePriceItem, ExchangeableSalesPrice> {

    @Override
    public ExchangeableSalesPrice process( @NonNull final ArticlePriceItem item ) {
        final ExchangeableSalesPrice exchangeableSalesPrice = new ExchangeableSalesPrice();

        exchangeableSalesPrice.setArticleId( item.getArticleId() );
        exchangeableSalesPrice.setChannel( item.getChannel() );
        exchangeableSalesPrice.setValue( item.getSellPrice() );

        return exchangeableSalesPrice;
    }
}
