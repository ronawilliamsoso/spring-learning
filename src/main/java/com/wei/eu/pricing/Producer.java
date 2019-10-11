package com.wei.eu.pricing;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Producer {
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
        props.put( "key.serializer",
                        "org.apache.kafka.common.serialization.StringSerializer" );
        props.put( "value.serializer",
                        "org.apache.kafka.common.serialization.StringSerializer" );

        final KafkaProducer<String, String> producer = new KafkaProducer<>( props );

        final String file = "/Users/wei.wang/Documents/prices_demo.csv";
        final BufferedReader reader = new BufferedReader( new FileReader( file ) );
        List list;
        final List<String> listPrices = new ArrayList<>();
        String currentLine = reader.readLine();
        Integer i = 0;
        final Integer max = 1000000;
        while ( currentLine != null && i < max ) {
            listPrices.add( currentLine );
            currentLine = reader.readLine();
            i++;
        }
        reader.close();
        System.out.println( "==========================> start to send " + listPrices.size() + " messages......" );
        final long startTime = System.currentTimeMillis();
        listPrices.forEach( s -> {
            producer.send( new ProducerRecord<>( "1p-2r", s ) );
        } );

        final long endTime = System.currentTimeMillis();
        System.out.println(
                        "==========================> sending " + listPrices.size() + " messages took： " + ( endTime - startTime ) + "ms" );
        producer.close();
    }
}