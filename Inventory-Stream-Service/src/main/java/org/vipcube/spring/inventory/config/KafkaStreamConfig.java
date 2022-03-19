package org.vipcube.spring.inventory.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.vipcube.spring.domain.dto.Order;
import org.vipcube.spring.inventory.entity.Product;

import java.util.Random;

@Slf4j
@Configuration
public class KafkaStreamConfig {
	private final Aggregator<Long, Order, Product> aggregator;
	private final Random random = new Random();

	public KafkaStreamConfig( Aggregator<Long, Order, Product> aggregator ) {
		this.aggregator = aggregator;
	}

	@Bean
	public KStream<Long, Order> stream( StreamsBuilder builder ) {
		JsonSerde<Order> orderSerde = new JsonSerde<>( Order.class );
		JsonSerde<Product> productSerde = new JsonSerde<>( Product.class );
		KStream<Long, Order> stream = builder.stream( "orders", Consumed.with( Serdes.Long(), orderSerde ) )
				.peek( ( k, order ) -> log.info( "InventoryKafkaStream: New: {}", order ) );

		KeyValueBytesStoreSupplier stockOrderStoreSupplier = Stores.persistentKeyValueStore( "stock-orders" );
		stream.selectKey( ( k, v ) -> v.getProductId() )
				.groupByKey( Grouped.with( Serdes.Long(), orderSerde ) )
				.aggregate( () -> Product.builder().availableItems( random.nextInt( 100 ) ).build(),
						this.aggregator,
						Materialized.<Long, Product>as( stockOrderStoreSupplier )
						.withKeySerde( Serdes.Long() )
						.withValueSerde( productSerde ) )
				.toStream()
				.peek( ( k, trx ) -> log.info( "InventoryKafkaStream: Commit: {}", trx ) );
		return stream;
	}
}
