package com.wei.eu.pricing.repository;

import com.wei.eu.pricing.model.SupplierArticle;
import com.wei.eu.pricing.model.keys.SupplierArticlePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierArticleRepository
                extends JpaRepository<SupplierArticle, SupplierArticlePK> {

}
