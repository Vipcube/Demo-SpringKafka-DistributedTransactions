package org.vipcube.spring.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@EnableKafkaStreams
@SpringBootApplication
public class PaymentStreamApplication {
	public static void main( String[] args ){
		SpringApplication.run( PaymentStreamApplication.class, args );
	}
}
