package org.vipcube.spring.inventory.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.vipcube.spring.domain.dto.Order;
import org.vipcube.spring.domain.dto.OrderStatus;

@Slf4j
@Service
public class InventoryProcessService {
	private final IOrderInventoryService orderInventoryService;

	public InventoryProcessService( IOrderInventoryService orderInventoryService ){
		this.orderInventoryService = orderInventoryService;
	}

	@KafkaListener( id = "orders", topics = "orders", groupId = "inventory")
	public void onOrder( Order order ) {
		log.info( "InventoryProcessService: Received order: {}" , order );
		if ( OrderStatus.NEW == order.getStatus() ) {
			this.orderInventoryService.reserve( order );
		} else {
			this.orderInventoryService.confirm( order );
		}
	}
}
