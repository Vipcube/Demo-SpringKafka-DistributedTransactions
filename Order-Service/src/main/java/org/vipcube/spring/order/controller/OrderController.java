package org.vipcube.spring.order.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vipcube.spring.domain.dto.Order;
import org.vipcube.spring.order.service.IOrderService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@RestController
@RequestMapping( "/orders" )
public class OrderController {
	private final AtomicLong id = new AtomicLong();
	private final KafkaTemplate<Long, Order> template;
	private final StreamsBuilderFactoryBean kafkaStreamsFactory;
	private final IOrderService orderService;

	public OrderController( KafkaTemplate<Long, Order> template, StreamsBuilderFactoryBean kafkaStreamsFactory,
			IOrderService orderService ) {
		this.template = template;
		this.kafkaStreamsFactory = kafkaStreamsFactory;
		this.orderService = orderService;
	}

	@PostMapping
	public Order create( @Validated @RequestBody Order order ) {
		order.setId( id.incrementAndGet() );
		template.send( "orders", order.getId(), order );
		log.info( "Sent: {}", order );
		return order;
	}

	@PostMapping( "/generate" )
	public boolean create() {
		this.orderService.generate();
		return true;
	}

	@GetMapping
	public List<Order> all() {
		List<Order> orders = new ArrayList<>();
		ReadOnlyKeyValueStore<Long, Order> store = kafkaStreamsFactory.getKafkaStreams()
				.store( StoreQueryParameters.fromNameAndType( "orders", QueryableStoreTypes.keyValueStore() ) );
		KeyValueIterator<Long, Order> it = store.all();
		it.forEachRemaining( kv -> orders.add( kv.value ) );
		return orders;
	}
}
