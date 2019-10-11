package com.wei.eu.pricing.model;

import com.wei.eu.pricing.model.keys.CrawlDataRawPK;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@ToString
@Entity
@IdClass( CrawlDataRawPK.class )
public class CrawlDataRaw {

    @Id
    @Column( length = 20, nullable = false )
    private String productId;

    @Id
    @Column( length = 100, nullable = false )
    private String seller;

    @Column( length = 30 )
    private String gtinEan;

    private String brand;

    private String manufacturerNr;

    private String title;

    private BigDecimal minPrice;

    private BigDecimal price;

    private BigDecimal maxPrice;

    private BigDecimal shippingCost;

    private Integer deliveryTime;

    private String attribute1;

    private String attribute2;

    private String attribute3;

    private String attribute4;

    private String attribute5;

    private String extra1;

    private String extra2;

    private String extra3;

    private String extra4;

    @Temporal( TemporalType.DATE )
    private Date date;

    private String classification;

    private Integer amountOfSellers;

    private String productNameChannel;

    private Integer reviews;

    private Integer ratingPercent;

    private BigDecimal ratingStars;

    private Integer deliveryTimeSeller;

    private BigDecimal priceWoShipping;

    private BigDecimal priceWithShipping;

    private String shippingCostSeller;

    private BigDecimal SuggestedRetailPrice;

    private String url;

    private String extraOne;

    private String extraTwo;

    private String extraThree;

}
