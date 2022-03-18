package org.vipcube.spring.payment.bean;

import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.vipcube.spring.payment.entity.CustomerFund;
import org.vipcube.spring.payment.repository.CustomerFundRepository;

import java.math.BigDecimal;
import java.util.Random;
import java.util.stream.IntStream;

@Component
public class TestDataGenerator implements CommandLineRunner {
	private final CustomerFundRepository repository;

	public TestDataGenerator( CustomerFundRepository repository ) {
		this.repository = repository;
	}

	@Override
	public void run( String... args ) throws Exception {
		Random r = new Random();
		Faker faker = new Faker();

		IntStream.range( 0, 100 )
				.forEach( i -> {
					int count = r.nextInt( 1000 );
					CustomerFund customerFund = CustomerFund.builder()
							.name( faker.name().fullName() )
							.amountAvailable( new BigDecimal( count ) )
							.amountReserved( BigDecimal.ZERO )
							.build();
					this.repository.save( customerFund );
				} );
	}
}
