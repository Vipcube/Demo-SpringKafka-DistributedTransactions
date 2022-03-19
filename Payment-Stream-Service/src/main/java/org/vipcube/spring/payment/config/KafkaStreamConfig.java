package org.vipcube.spring.payment.config;

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
import org.vipcube.spring.payment.entity.CustomerFund;

import java.math.BigDecimal;
import java.util.Random;

@Slf4j
@Configuration
public class KafkaStreamConfig {
	private final Aggregator<Long, Order, CustomerFund> aggregator;
	private final Random random = new Random();

	public KafkaStreamConfig( Aggregator<Long, Order, CustomerFund> aggregator ) {
		this.aggregator = aggregator;
	}

	@Bean
	public KStream<Long, Order> stream( StreamsBuilder builder ) {
		JsonSerde<Order> orderSerde = new JsonSerde<>( Order.class );
		JsonSerde<CustomerFund> customerFundSerde = new JsonSerde<>( CustomerFund.class );
		KStream<Long, Order> stream = builder.stream( "orders", Consumed.with( Serdes.Long(), orderSerde ) )
				.peek( ( k, order ) -> log.info( "PaymentKafkaStream: New: {}", order ) );

		KeyValueBytesStoreSupplier customerOrderStoreSupplier = Stores.persistentKeyValueStore( "customer-orders" );

		stream.selectKey( ( k, v ) -> v.getCustomerId() )
				.groupByKey( Grouped.with( Serdes.Long(), orderSerde ) )
				.aggregate( () -> CustomerFund.builder().amountAvailable( new BigDecimal( random.nextInt( 1000 ) ) ).build(),
						this.aggregator,
						Materialized.<Long, CustomerFund>as( customerOrderStoreSupplier )
						.withKeySerde( Serdes.Long() )
						.withValueSerde( customerFundSerde ) )
				.toStream()
				.peek( ( k, trx ) -> log.info( "PaymentKafkaStream: Commit: {}", trx ) );
		return stream;
	}
}
