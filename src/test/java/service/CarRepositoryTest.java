package service;

import com.wei.eu.pricing.PricingApplication;
import com.wei.eu.pricing.model.Car;
import com.wei.eu.pricing.model.CarRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@SpringBootTest( classes = PricingApplication.class )
class CarRepositoryTest {

    @Autowired
    CarRepository carRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    void findByName() {

        final Car savedCar = carRepository.save( new Car( "prius", "hybrid" ) );

        final Car car = carRepository.findByName( "prius" );

        assertThat( car.getName() ).isEqualTo( savedCar.getName() );
        assertThat( car.getType() ).isEqualTo( savedCar.getType() );
        assertThat( car.getId() ).isEqualTo( savedCar.getId() );
    }

    @Test
    void findByName_with_entityManager() {

        final Car savedCar = entityManager.persistFlushFind( new Car( "prius", "hybrid" ) );

        final Car car = carRepository.findByName( "prius" );

        assertThat( car.getName() ).isEqualTo( savedCar.getName() );
        assertThat( car.getType() ).isEqualTo( savedCar.getType() );
        assertThat( car.getId() ).isEqualTo( savedCar.getId() );
    }
}