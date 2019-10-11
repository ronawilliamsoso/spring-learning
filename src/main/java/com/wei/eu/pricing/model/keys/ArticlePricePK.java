package com.wei.eu.pricing.model.keys;

import com.zoro.eu.domain.enums.Channel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

@Getter
@Setter
@Embeddable
@EqualsAndHashCode
public class ArticlePricePK
                implements Serializable {

    private String articleId;

    @Enumerated( EnumType.STRING )
    private Channel channel;

}

