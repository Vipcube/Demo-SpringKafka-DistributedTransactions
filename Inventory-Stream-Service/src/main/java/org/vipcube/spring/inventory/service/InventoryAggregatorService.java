package org.vipcube.spring.inventory.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.Aggregator;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.vipcube.spring.domain.dto.Order;
import org.vipcube.spring.domain.dto.OrderStatus;
import org.vipcube.spring.domain.dto.ServiceSource;
import org.vipcube.spring.inventory.entity.Product;

import java.util.Objects;

@Slf4j
@Service
public class InventoryAggregatorService implements Aggregator<Long, Order, Product> {
	private final KafkaTemplate<Long, Order> template;

	public InventoryAggregatorService( KafkaTemplate<Long, Order> template ) {
		this.template = template;
	}

	@Override
	public Product apply( Long aLong, Order order, Product product ) {
		switch ( order.getStatus() ) {
		case CONFIRMED:
			product.setReservedItems( product.getReservedItems() - order.getProductCount() );
			break;
		case ROLLBACK:
			if ( ServiceSource.INVENTORY != order.getSource() ) {
				product.setReservedItems( product.getReservedItems() - order.getProductCount() );
				product.setAvailableItems( product.getAvailableItems() + order.getProductCount() );
			}
			break;
		case NEW:
			order.setSource( ServiceSource.INVENTORY );
			if ( order.getProductCount() < product.getAvailableItems() ) {
				product.setReservedItems( product.getReservedItems() + order.getProductCount() );
				product.setAvailableItems( product.getAvailableItems() - order.getProductCount() );
				order.setStatus( OrderStatus.ACCEPT );
			} else {
				order.setStatus( OrderStatus.REJECT );
			}
			template.send( "inventory-orders", order.getId(), order )
					.addCallback( result -> log.info( "InventoryAggregatorService: Sent: {}",
							Objects.nonNull( result ) ?
									result.getProducerRecord()
											.value() :
									null ), ex -> {
					} );
			break;
		}
		return product;
	}
}
