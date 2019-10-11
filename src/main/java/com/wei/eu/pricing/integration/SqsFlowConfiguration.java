package com.wei.eu.pricing.integration;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.wei.eu.pricing.integration.transform.ExchangeableObjectTransformer;
import com.zoro.eu.domain.inventory.ExchangeableInventoryItem;
import com.zoro.eu.domain.product.ExchangeableArticle;
import com.zoro.eu.domain.product.ExchangeableDeletedArticle;
import com.zoro.eu.domain.product.ExchangeableSupplierArticle;
import com.wei.eu.pricing.configuration.PricingProperties;
import com.wei.eu.pricing.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.aws.inbound.SqsMessageDrivenChannelAdapter;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;

@Configuration
public class SqsFlowConfiguration {

    private final PricingProperties pricingProperties;

    @Autowired
    public SqsFlowConfiguration( final PricingProperties pricingProperties ) {
        this.pricingProperties = pricingProperties;
    }

    @Bean
    public IntegrationFlow receiveExchangeableItemFromSqsFlow( final AmazonSQSAsync amazonSQSAsync, final ArticleService articleService ) {
        return IntegrationFlows.from( getSqsMessageDrivenChannelAdapter( amazonSQSAsync ) )
                        .transform( new ExchangeableObjectTransformer() )
                        .<Object, Class<?>>route( Object::getClass,
                                        routerSpec -> routerSpec
                                                        .subFlowMapping( ExchangeableArticle.class,
                                                                        flow -> flow.handle( articleService,
                                                                                        "handleExchangeableArticle" ) )
                                                        .subFlowMapping( ExchangeableSupplierArticle.class,
                                                                        flow -> flow.handle( articleService,
                                                                                        "handleExchangeableSupplierArticle" ) )
                                                        .subFlowMapping( ExchangeableDeletedArticle.class,
                                                                        flow -> flow.handle( articleService,
                                                                                        "handleExchangeableDeletedArticle" ) )
                                                        .subFlowMapping( ExchangeableInventoryItem.class,
                                                                        flow -> flow.handle( articleService,
                                                                                        "handleExchangeableInventoryItem" ) )
                                                        .defaultOutputChannel( "nullChannel" ) )
                        .get();
    }

    private SqsMessageDrivenChannelAdapter getSqsMessageDrivenChannelAdapter( final AmazonSQSAsync amazonSQSAsync ) {
        final SqsMessageDrivenChannelAdapter channelAdapter =
                        new SqsMessageDrivenChannelAdapter( amazonSQSAsync, pricingProperties.getInboundQueueName() );
        channelAdapter.setMessageDeletionPolicy( SqsMessageDeletionPolicy.ON_SUCCESS );
        channelAdapter.setMaxNumberOfMessages( pricingProperties.getMaxNumberOfMessages() );
        channelAdapter.setBeanName( "sqsMessageDrivenChannelAdapter" );

        return channelAdapter;
    }
}
