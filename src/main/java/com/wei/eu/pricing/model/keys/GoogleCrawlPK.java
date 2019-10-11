package com.wei.eu.pricing.model.keys;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@Embeddable
@EqualsAndHashCode
public class GoogleCrawlPK
                implements Serializable {

    private String productId;

    private String seller;

}

