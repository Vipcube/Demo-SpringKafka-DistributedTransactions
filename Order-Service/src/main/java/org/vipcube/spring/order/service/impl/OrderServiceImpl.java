package org.vipcube.spring.order.service.impl;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.vipcube.spring.domain.dto.Order;
import org.vipcube.spring.domain.dto.OrderStatus;
import org.vipcube.spring.domain.dto.ServiceSource;
import org.vipcube.spring.order.service.IOrderService;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

@Service
public class OrderServiceImpl implements IOrderService {
	private static final ThreadLocalRandom RAND = ThreadLocalRandom.current();
	private final AtomicLong id = new AtomicLong();
	private final KafkaTemplate<Long, Order> template;

	public OrderServiceImpl( KafkaTemplate<Long, Order> template ) {
		this.template = template;
	}

	@Async
	@Override
	public void generate() {
		IntStream.range( 0, 10000 )
				.map( i -> RAND.nextInt( 5 ) + 1 )
				.mapToObj( random -> Order.builder()
						.id( id.incrementAndGet() )
						.customerId( RAND.nextLong( 100 ) + 1 )
						.productId( RAND.nextLong( 100 ) + 1 )
						.price( new BigDecimal( 100 * random ) )
						.productCount( random )
						.status( OrderStatus.NEW )
						.build() )
				.forEach( order -> this.template.send( "orders", order.getId(), order ) );
	}

	@Override
	public Order confirm( Order orderPayment, Order orderInventory ) {
		Order order = Order.builder()
				.id( orderPayment.getId() )
				.customerId( orderPayment.getCustomerId() )
				.productId( orderPayment.getProductId() )
				.productCount( orderPayment.getProductCount() )
				.price( orderPayment.getPrice() )
				.build();

		if ( OrderStatus.ACCEPT == orderPayment.getStatus() && OrderStatus.ACCEPT == orderInventory.getStatus() ) {
			order.setStatus( OrderStatus.CONFIRMED );
		} else if ( OrderStatus.REJECT == orderPayment.getStatus()
				&& OrderStatus.REJECT == orderInventory.getStatus() ) {
			order.setStatus( OrderStatus.REJECT );
		} else if ( OrderStatus.REJECT == orderPayment.getStatus()
				|| OrderStatus.REJECT == orderInventory.getStatus() ) {
			ServiceSource source =
					OrderStatus.REJECT == orderPayment.getStatus() ? ServiceSource.PAYMENT : ServiceSource.INVENTORY;
			order.setStatus( OrderStatus.ROLLBACK );
			order.setSource( source );
		}
		return order;
	}
}
