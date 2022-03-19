package org.vipcube.spring.inventory.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.vipcube.spring.domain.dto.Order;
import org.vipcube.spring.domain.dto.OrderStatus;
import org.vipcube.spring.domain.dto.ServiceSource;
import org.vipcube.spring.inventory.entity.Product;
import org.vipcube.spring.inventory.repository.ProductRepository;
import org.vipcube.spring.inventory.service.IOrderInventoryService;

@Slf4j
@Service
public class OrderInventoryServiceImpl implements IOrderInventoryService {
	private final ProductRepository repository;
	private final KafkaTemplate<Long, Order> template;

	public OrderInventoryServiceImpl( ProductRepository repository, KafkaTemplate<Long, Order> template ) {
		this.repository = repository;
		this.template = template;
	}

	@Override
	public void confirm( Order order ) {
		Product product = this.repository.findById( order.getProductId() )
				.orElseThrow();
		log.info( "OrderInventoryService: Found product: {}", product );
		if ( OrderStatus.CONFIRMED == order.getStatus() ) {
			product.setReservedItems( product.getReservedItems() - order.getProductCount() );
			this.repository.save( product );
		} else if ( OrderStatus.ROLLBACK == order.getStatus() && ServiceSource.INVENTORY != order.getSource() ) {
			product.setReservedItems( product.getReservedItems() - order.getProductCount() );
			product.setAvailableItems( product.getAvailableItems() + order.getProductCount() );
			this.repository.save( product );
		}
	}

	@Override
	public void reserve( Order order ) {
		Product product = this.repository.findById( order.getProductId() )
				.orElseThrow();
		log.info( "OrderInventoryService: Found product: {}", product );

		order.setSource( ServiceSource.INVENTORY );
		if ( order.getProductCount() < product.getAvailableItems() ) {
			product.setReservedItems( product.getReservedItems() + order.getProductCount() );
			product.setAvailableItems( product.getAvailableItems() - order.getProductCount() );
			order.setStatus( OrderStatus.ACCEPT );
			this.repository.save( product );
		} else {
			order.setStatus( OrderStatus.REJECT );
		}
		template.send( "inventory-orders", order.getId(), order );
		log.info( "OrderInventoryService: Send customer order: {}", order );
	}
}
