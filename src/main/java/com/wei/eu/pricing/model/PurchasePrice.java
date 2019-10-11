package com.wei.eu.pricing.model;

import com.wei.eu.pricing.model.keys.PurchasePricePK;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@ToString
@Entity
@SelectBeforeUpdate
@DynamicUpdate
@EqualsAndHashCode( of = { "purchasePricePK" } )
@Table( name = "purchase_price" )
public class PurchasePrice {

    @ManyToOne
    @JoinColumns( {
                    @JoinColumn( name = "supplierId", referencedColumnName = "supplierId", insertable = false, updatable = false ),
                    @JoinColumn( name = "supplierArticleId", referencedColumnName = "supplierArticleId", insertable = false, updatable = false )
    } )
    public SupplierArticle supplierArticle;

    @Id
    private PurchasePricePK purchasePricePK;

    @Temporal( TemporalType.DATE )
    private Date validToDate;

    @Column( nullable = false )
    private BigDecimal value;

    private String currency;

}
