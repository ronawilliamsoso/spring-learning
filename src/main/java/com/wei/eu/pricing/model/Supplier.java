package com.wei.eu.pricing.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@Table( name = "supplier" )
public class Supplier {

    @Id
    @Column( unique = true )
    private Integer supplierId;

    private String supplierName;

    private String supplierShortName;

    public Supplier( final Integer supplierId ) {
        this.supplierId = supplierId;
    }
}
