package com.wei.eu.pricing.model;

import com.wei.eu.pricing.model.keys.ArticlePricePK;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@ToString
@SelectBeforeUpdate
@DynamicUpdate
@EqualsAndHashCode( of = { "articlePricePK" } )
@Entity
@Table( name = "article_price" )
public class ArticlePrice {

    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "articleId", referencedColumnName = "articleId", insertable = false, updatable = false )
    public Article article;

    @Id
    private ArticlePricePK articlePricePK;

    private BigDecimal sellPrice;

    @Column( insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" )
    @Temporal( TemporalType.TIMESTAMP )
    private Date lastModified;

    private ArticlePriceState itemState;
}
