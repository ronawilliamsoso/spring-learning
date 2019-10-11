package com.wei.eu.pricing.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
public class Car {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String type;

    public Car( final String name, final String type ) {
        this.name = name;
        this.type = type;
    }
}
