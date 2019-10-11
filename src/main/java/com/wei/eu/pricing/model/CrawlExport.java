package com.wei.eu.pricing.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Id;

@Getter
@Setter
@ToString
public class CrawlExport {

    @Id
    private String product_id;

    private String gtin_ean;

    private String brand;

    private String manufacturer_nr;

    private String title;

    private String min_price;

    private String price;

    private String max_price;

    private String shipping_cost;

    private String delivery_time;

    private String attribute_1;

    private String attribute_2;

    private String attribute_3;

    private String attribute_4;

    private String attribute_5;

    private String extra_1;

    private String extra_2;

    private String extra_3;

    private String extra_4;

    private String OPS;
}
