package com.wei.eu.pricing.batch.model;

import com.wei.eu.pricing.model.ArticlePriceState;
import com.zoro.eu.domain.enums.Channel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ArticlePriceItem {

    private String articleId;

    private Channel channel;

    private BigDecimal sellPrice;

    private ArticlePriceState itemState;
}
