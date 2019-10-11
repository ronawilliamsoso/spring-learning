package com.wei.eu.pricing.model;

import com.wei.eu.pricing.model.keys.SupplierArticlePK;
import com.zoro.eu.domain.enums.BusinessModel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@SelectBeforeUpdate
@DynamicUpdate
@EqualsAndHashCode( of = { "supplierArticlePK" } )
@Table( name = "supplier_article" )
public class SupplierArticle {

    @OneToMany( cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "supplierArticle", fetch = FetchType.EAGER )
    private Set<PurchasePrice> purchasePrices = new HashSet<>();

    @OneToOne( cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "supplierArticle", fetch = FetchType.LAZY )
    private SupplierZoroRelation supplierZoroRelation;

    @Id
    private SupplierArticlePK supplierArticlePK;

    @Enumerated( EnumType.STRING )
    private BusinessModel businessModel;

    private Boolean forwardingAgency;

    private String purchaseOrderUnit;

    private String purchaseContentUnit;

    public SupplierArticle( final SupplierArticlePK supplierArticlePK ) {
        this.supplierArticlePK = supplierArticlePK;
    }

    public void addArticlePrice( final PurchasePrice purchasePrice ) {
        if ( purchasePrices == null ) {
            purchasePrices = new HashSet<>();
        }

        purchasePrices.add( purchasePrice );
        purchasePrice.setSupplierArticle( this );
    }

    public void replacePurchasePrices( final Set<PurchasePrice> purchasePrices ) {
        if ( this.purchasePrices == null ) {
            this.purchasePrices = new HashSet<>();
        }
        this.purchasePrices.clear();
        for ( final PurchasePrice purchasePrice : purchasePrices ) {
            addArticlePrice( purchasePrice );
        }
    }

    public void setSupplierRelation( final SupplierZoroRelation supplierZoroRelation ) {
        this.supplierZoroRelation = supplierZoroRelation;
        supplierZoroRelation.setSupplierArticle( this );
    }
}
