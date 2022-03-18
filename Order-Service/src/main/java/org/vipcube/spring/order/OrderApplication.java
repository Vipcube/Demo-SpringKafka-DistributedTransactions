package org.vipcube.spring.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@EnableKafkaStreams
@SpringBootApplication
public class OrderApplication {
	public static void main( String[] args ){
		SpringApplication.run( OrderApplication.class, args );
	}
}
