package org.vipcube.spring.inventory.bean;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.vipcube.spring.inventory.entity.Product;
import org.vipcube.spring.inventory.repository.ProductRepository;

import java.util.Random;
import java.util.stream.IntStream;

@Component
public class TestDataGenerator implements CommandLineRunner {
	private final ProductRepository repository;

	public TestDataGenerator( ProductRepository repository ) {
		this.repository = repository;
	}

	@Override
	public void run( String... args ) throws Exception {
		Random r = new Random();
		IntStream.range( 0, 1000 )
				.forEach( i -> {
					int count = r.nextInt( 1000 );
					Product product = Product.builder()
							.name( "Product_" + i )
							.availableItems( count )
							.reservedItems( 0 )
							.build();
					this.repository.save( product );
				} );
	}
}
