package com.wei.eu.pricing.model;

import com.zoro.eu.domain.enums.Location;
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
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@SelectBeforeUpdate
@DynamicUpdate
@EqualsAndHashCode( of = { "articleId" } )
@Table( name = "article" )
public class Article {

    @Id
    private String articleId;

    @Enumerated( EnumType.STRING )
    private Location location;

    private BigDecimal averageWarehouseCost;

    @OneToMany( cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "article", fetch = FetchType.EAGER )
    private Set<SupplierZoroRelation> supplierZoroRelations = new HashSet<>();

    @OneToMany( cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "article", fetch = FetchType.LAZY )
    private Set<ArticlePrice> articlePriceSet = new HashSet<>();

    public Article( final String articleId ) {
        this.articleId = articleId;
    }

    public void addSupplierRelation( final SupplierZoroRelation supplierZoroRelation ) {
        supplierZoroRelations.add( supplierZoroRelation );
        supplierZoroRelation.setArticle( this );
    }

    public void replaceSupplierRelations( final Set<SupplierZoroRelation> supplierZoroRelations ) {
        if ( this.supplierZoroRelations == null ) {
            this.supplierZoroRelations = new HashSet<>();
        }
        this.supplierZoroRelations.clear();
        for ( final SupplierZoroRelation relation : supplierZoroRelations ) {
            addSupplierRelation( relation );
        }
    }
}
