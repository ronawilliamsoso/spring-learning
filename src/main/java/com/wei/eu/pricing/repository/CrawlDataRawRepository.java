package com.wei.eu.pricing.repository;

import com.wei.eu.pricing.model.CrawlDataRaw;
import com.wei.eu.pricing.model.keys.CrawlDataRawPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrawlDataRawRepository
                extends JpaRepository<CrawlDataRaw, CrawlDataRawPK> {

}