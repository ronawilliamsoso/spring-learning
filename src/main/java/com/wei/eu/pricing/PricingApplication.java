package com.wei.eu.pricing;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableBatchProcessing
@SpringBootApplication
@EnableDiscoveryClient
public class PricingApplication {

    public static void main( final String[] args ) {
        SpringApplication.run( PricingApplication.class, args );
    }
}
