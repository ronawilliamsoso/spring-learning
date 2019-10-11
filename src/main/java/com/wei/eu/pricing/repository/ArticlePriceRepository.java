package com.wei.eu.pricing.repository;

import com.wei.eu.pricing.model.ArticlePrice;
import com.wei.eu.pricing.model.keys.ArticlePricePK;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ArticlePriceRepository
                extends JpaRepository<ArticlePrice, ArticlePricePK> {

    List<ArticlePrice> findAllByLastModifiedAfter( Date time, Pageable pageable );
}
