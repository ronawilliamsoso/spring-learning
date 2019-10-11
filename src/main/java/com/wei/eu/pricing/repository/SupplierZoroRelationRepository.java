package com.wei.eu.pricing.repository;

import com.wei.eu.pricing.model.SupplierZoroRelation;
import com.wei.eu.pricing.model.keys.RelationPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierZoroRelationRepository
                extends JpaRepository<SupplierZoroRelation, RelationPK> {

}
