package org.vipcube.spring.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@EnableKafkaStreams
@SpringBootApplication
public class InventoryStreamApplication {
	public static void main( String[] args ){
		SpringApplication.run( InventoryStreamApplication.class, args );
	}
}
