package com.wei.eu.pricing.service;

import com.wei.eu.pricing.model.PurchasePrice;
import com.wei.eu.pricing.model.Supplier;
import com.wei.eu.pricing.model.SupplierArticle;
import com.wei.eu.pricing.model.SupplierZoroRelation;
import com.wei.eu.pricing.model.keys.PurchasePricePK;
import com.wei.eu.pricing.model.keys.RelationPK;
import com.wei.eu.pricing.model.keys.SupplierArticlePK;
import com.wei.eu.pricing.repository.SupplierArticleRepository;
import com.wei.eu.pricing.repository.SupplierRepository;
import com.zoro.eu.domain.inventory.ExchangeableInventoryItem;
import com.zoro.eu.domain.product.ExchangeableArticle;
import com.zoro.eu.domain.product.ExchangeableDeletedArticle;
import com.zoro.eu.domain.product.ExchangeableSupplierArticle;
import com.zoro.eu.domain.product.entities.SalesPrice;
import com.zoro.eu.domain.product.entities.SupplierRelation;
import com.wei.eu.pricing.model.Article;
import com.wei.eu.pricing.repository.ArticleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Optional.ofNullable;

@Service
@AllArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    private final SupplierArticleRepository supplierArticleRepository;

    private final SupplierRepository supplierRepository;

    @Transactional( rollbackFor = Exception.class )
    public void handleExchangeableArticle( @Valid final ExchangeableArticle input ) {

        final Article article = articleRepository.findById( input.getArticleId() ).orElse( new Article( input.getArticleId() ) );
        final Set<SupplierZoroRelation> supplierRelations = new HashSet<>();

        ofNullable( input.getSupplierRelations() )
                        .ifPresent( relations ->
                                        relations.forEach( relation -> supplierRelations
                                                        .add( createSupplierRelation( relation, input ) ) ) );

        article.replaceSupplierRelations( supplierRelations );
        articleRepository.save( article );
    }

    @Transactional( rollbackFor = Exception.class )
    public void handleExchangeableSupplierArticle( @Valid final ExchangeableSupplierArticle input ) {
        if ( input.getSupplierId() != null ) {
            createOrUpdateSupplier( input );
            createOrUpdateSupplierArticle( input );
        }
    }

    @Transactional( rollbackFor = Exception.class )
    public void handleExchangeableInventoryItem( @Valid final ExchangeableInventoryItem input ) {

        final Article article = articleRepository.findById( input.getArticleId() )
                        .orElse( new Article( input.getArticleId() ) );

        article.setLocation( input.getLocation() );
        article.setAverageWarehouseCost( input.getAverageWarehouseCost() );

        articleRepository.save( article );
    }

    public void handleExchangeableDeletedArticle( @Valid final ExchangeableDeletedArticle input ) {
        articleRepository.findById( input.getArticleId() ).ifPresent( articleRepository::delete );
    }

    private SupplierZoroRelation createSupplierRelation( final SupplierRelation relation, final ExchangeableArticle input ) {
        createOrUpdateSupplier( relation );

        final SupplierArticle supplierArticle = getSupplierArticle( relation );
        final RelationPK relationPK = new RelationPK();

        relationPK.setArticleId( input.getArticleId() );
        relationPK.setSupplierArticlePK( supplierArticle.getSupplierArticlePK() );

        SupplierZoroRelation supplierZoroRelation = supplierArticle.getSupplierZoroRelation();
        if ( supplierZoroRelation == null || !relationPK.equals( supplierZoroRelation.getRelationPK() ) ) {
            supplierZoroRelation = new SupplierZoroRelation();
            supplierZoroRelation.setRelationPK( relationPK );
        }

        supplierArticle.setSupplierRelation( supplierZoroRelation );
        supplierZoroRelation.setMainSupplier( relation.getSupplierId().equals( input.getMainSupplierId() ) );
        supplierZoroRelation.setRelationActive( Boolean.TRUE.equals( relation.isActive() ) );

        return supplierZoroRelation;
    }

    private SupplierArticle getSupplierArticle( final SupplierRelation relation ) {
        final SupplierArticlePK supplierArticlePK = new SupplierArticlePK();
        supplierArticlePK.setSupplierId( relation.getSupplierId() );
        supplierArticlePK.setSupplierArticleId( relation.getSupplierAid() );

        return supplierArticleRepository.findById( supplierArticlePK ).orElse( new SupplierArticle( supplierArticlePK ) );

    }

    private void createOrUpdateSupplier( final SupplierRelation supplierRelation ) {
        final Supplier supplier = supplierRepository
                        .findById( supplierRelation.getSupplierId() ).orElse( new Supplier( supplierRelation.getSupplierId() ) );
        supplier.setSupplierName( supplierRelation.getSupplierName() );
        supplier.setSupplierShortName( supplierRelation.getSupplierShortName() );

        supplierRepository.save( supplier );
    }

    private void createOrUpdateSupplier( final ExchangeableSupplierArticle input ) {
        final Supplier supplier = supplierRepository
                        .findById( input.getSupplierId() ).orElse( new Supplier( input.getSupplierId() ) );
        supplier.setSupplierName( input.getSupplierName() );
        supplier.setSupplierShortName( input.getSupplierShortName() );

        supplierRepository.save( supplier );
    }

    private void createOrUpdateSupplierArticle( final ExchangeableSupplierArticle input ) {
        final SupplierArticlePK supplierArticlePK = new SupplierArticlePK();
        supplierArticlePK.setSupplierId( input.getSupplierId() );
        supplierArticlePK.setSupplierArticleId( input.getArticleId() );

        final SupplierArticle supplierArticle =
                        supplierArticleRepository.findById( supplierArticlePK ).orElse( new SupplierArticle( supplierArticlePK ) );
        supplierArticle.setBusinessModel( input.getBusinessModel() );
        supplierArticle.setForwardingAgency( input.getForwardingAgency() );
        supplierArticle.setPurchaseContentUnit( input.getPurchaseContentUnit() );
        supplierArticle.setPurchaseOrderUnit( input.getPurchaseOrderUnit() );

        final Set<PurchasePrice> purchasePrices = new HashSet<>();
        final List<SalesPrice> purchasePrice = input.getPurchasePrice();
        if ( !CollectionUtils.isEmpty( purchasePrice ) ) {
            purchasePrice.stream()
                            .filter( price -> price.getValue() != null ) // do not store price entries without value
                            .forEach( price -> purchasePrices.add( getPurchasePrices( price, supplierArticlePK ) ) );
        }

        supplierArticle.replacePurchasePrices( purchasePrices );

        supplierArticleRepository.save( supplierArticle );
    }

    private PurchasePrice getPurchasePrices( final SalesPrice salesPrice, final SupplierArticlePK supplierArticlePK ) {

        final PurchasePrice purchasePrice = new PurchasePrice();
        final PurchasePricePK purchasePricePK = new PurchasePricePK();

        purchasePricePK.setSupplierArticlePK( supplierArticlePK );
        purchasePricePK.setSalePriceType( salesPrice.getSalePriceType() );
        purchasePricePK.setValidFromDate( salesPrice.getValidFromDate() );
        purchasePrice.setPurchasePricePK( purchasePricePK );

        purchasePrice.setValidToDate( salesPrice.getValidToDate() );
        purchasePrice.setValue( salesPrice.getValue() );
        purchasePrice.setCurrency( salesPrice.getCurrency() );

        return purchasePrice;

    }
}