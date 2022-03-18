package org.vipcube.spring.order.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.vipcube.spring.domain.dto.Order;
import org.vipcube.spring.order.service.IOrderService;

import java.time.Duration;

@Slf4j
@Configuration
public class KafkaStreamConfig {
	private final IOrderService orderService;

	public KafkaStreamConfig( IOrderService orderService ) {
		this.orderService = orderService;
	}

	@Bean
	public KStream<Long, Order> stream( StreamsBuilder builder ) {
		JsonSerde<Order> orderSerde = new JsonSerde<>( Order.class );
		KStream<Long, Order> stream = builder.stream( "payment-orders", Consumed.with( Serdes.Long(), orderSerde ) );

		stream.join( builder.stream( "inventory-orders" ), this.orderService::confirm,
						JoinWindows.of( Duration.ofSeconds( 10 ) ), StreamJoined.with( Serdes.Long(), orderSerde, orderSerde ) )
				.peek( ( k, o ) -> log.info( "OrderService: Current stream order: {}", o ) )
				.to( "orders" );
		return stream;
	}
}
