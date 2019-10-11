package com.wei.eu.pricing.repository;

import com.wei.eu.pricing.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository
                extends JpaRepository<Supplier, Integer> {

}
