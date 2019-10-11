package com.wei.eu.pricing.integration.transform;

import com.zoro.eu.domain.ExchangeableItem;
import com.zoro.eu.domain.converter.ExchangeableConverter;
import org.springframework.integration.transformer.AbstractTransformer;
import org.springframework.messaging.Message;

public class ExchangeableObjectTransformer
                extends AbstractTransformer {

    private final ExchangeableConverter converter;

    public ExchangeableObjectTransformer() {
        converter = new ExchangeableConverter();
    }

    @Override
    protected ExchangeableItem doTransform( final Message<?> message ) {
        return (ExchangeableItem) converter.fromMessage( message, ExchangeableItem.class );
    }
}
