package com.wei.eu.pricing.model.keys;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
public class CrawlDataRawPK
                implements Serializable {

    private String productId;

    private String seller;

}

