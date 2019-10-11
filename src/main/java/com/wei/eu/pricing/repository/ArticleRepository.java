package com.wei.eu.pricing.repository;

import com.wei.eu.pricing.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository
                extends JpaRepository<Article, String> {

}
