package com.wei.eu.pricing.repository;

import com.wei.eu.pricing.model.PurchasePrice;
import com.wei.eu.pricing.model.keys.PurchasePricePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchasePriceRepository
                extends JpaRepository<PurchasePrice, PurchasePricePK> {

}
