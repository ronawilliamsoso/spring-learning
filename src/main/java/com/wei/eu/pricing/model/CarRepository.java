package com.wei.eu.pricing.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository
                extends CrudRepository<Car, Long> {

    /*
    Inferred Queries

    What’s nice about this feature is that Spring Data also automatically checks if the query is valid at startup.
    If we renamed the method to findByFoo() and the Car Entity does not have a property foo,
    Spring Data will point that out to us with an exception:

        org.springframework.data.mapping.PropertyReferenceException:
            No property foo found for type Car!

     */
    Car findByName( String name );

    // Bad example
    Car findByNameAndNameContainingOrTypeOrderByType( String name, String nameContaining, String type );

    /*
    Custom JPQL Queries with @Query

    If queries become more complex, it makes sense to provide a custom JPQL query

    Similar to inferred queries, we get a validity check for those JPQL queries for free.
    Using Hibernate as our JPA provider, we’ll get a QuerySyntaxException on startup
    if it found an invalid query:

    org.hibernate.hql.internal.ast.QuerySyntaxException:
        unexpected token: foo near line 1, column 64 [select u from ...]

     */
    @Query( "select c from Car c where c.name = :name" )
    Car findByNameCustomQuery( @Param( "name" ) String name );
}
