package com.wei.eu.pricing.model.keys;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@Embeddable
@EqualsAndHashCode
public class RelationPK
                implements Serializable {

    private SupplierArticlePK supplierArticlePK;

    private String articleId;

}

