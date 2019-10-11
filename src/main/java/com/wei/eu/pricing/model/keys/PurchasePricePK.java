package com.wei.eu.pricing.model.keys;

import com.zoro.eu.domain.enums.SalePriceType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Embeddable
@EqualsAndHashCode
public class PurchasePricePK
                implements Serializable {

    private SupplierArticlePK supplierArticlePK;

    @Enumerated( EnumType.STRING )
    private SalePriceType salePriceType;

    @Temporal( TemporalType.DATE )
    private Date validFromDate;

}

