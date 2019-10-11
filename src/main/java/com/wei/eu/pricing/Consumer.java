package com.wei.eu.pricing;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

public class Consumer {
    public static void main( final String[] args )
                    throws IOException {
        final Properties props = new Properties();
        props.put( "bootstrap.servers", "kafka-weiwang-weiwang2702-6f31.aivencloud.com:18094" );
        props.put( "security.protocol", "SSL" );
        props.put( "ssl.endpoint.identification.algorithm", "" );
        props.put( "ssl.truststore.location", "client.truststore.jks" );
        props.put( "ssl.truststore.password", "123456" );
        props.put( "ssl.keystore.type", "PKCS12" );
        props.put( "ssl.keystore.location", "client.keystore.p12" );
        props.put( "ssl.keystore.password", "123456" );
        props.put( "ssl.key.password", "123456" );
        props.put( "group.id", "demo-group" );
        props.put( "key.deserializer",
                        "org.apache.kafka.common.serialization.StringDeserializer" );
        props.put( "value.deserializer",
                        "org.apache.kafka.common.serialization.StringDeserializer" );
        final KafkaConsumer<String, String> consumer = new KafkaConsumer<>( props );
        consumer.subscribe( Arrays.asList( "3p-3r" ) );
        while ( true ) {
            final ConsumerRecords<String, String> records = consumer.poll( 1000 );
            if ( !records.isEmpty() ) {
                for ( final ConsumerRecord<String, String> record : records ) {
                    System.out.println( "======>" + record.value() );
                    //                System.out.printf( "offset = %d, key = %s, value = %s",
                    //                                record.offset(), record.key(), record.value() );
                }

            }

        }
    }
}