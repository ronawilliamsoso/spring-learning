package com.wei.eu.pricing.model;

import com.wei.eu.pricing.model.keys.RelationPK;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Getter
@Setter
@ToString
@Entity
@SelectBeforeUpdate
@DynamicUpdate
@EqualsAndHashCode( of = { "relationPK" } )
@Table( name = "supplier_zoro_relation" )
public class SupplierZoroRelation {

    @MapsId( "articleId" )
    @ManyToOne( cascade = { CascadeType.PERSIST, CascadeType.MERGE } )
    @JoinColumn( name = "articleId", referencedColumnName = "articleId" )
    public Article article;

    @OneToOne( cascade = { CascadeType.PERSIST, CascadeType.MERGE } )
    @JoinColumns( {
                    @JoinColumn( name = "supplierId", referencedColumnName = "supplierId", insertable = false, updatable = false ),
                    @JoinColumn( name = "supplierArticleId", referencedColumnName = "supplierArticleId", insertable = false, updatable = false )
    } )
    public SupplierArticle supplierArticle;

    @EmbeddedId
    private RelationPK relationPK;

    @Column( nullable = false )
    private boolean mainSupplier;

    @Column( nullable = false )
    private boolean relationActive;

}
