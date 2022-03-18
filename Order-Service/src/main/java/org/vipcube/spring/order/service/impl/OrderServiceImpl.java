package org.vipcube.spring.order.service.impl;

import org.springframework.stereotype.Service;
import org.vipcube.spring.domain.dto.Order;
import org.vipcube.spring.domain.dto.OrderStatus;
import org.vipcube.spring.domain.dto.ServiceSource;
import org.vipcube.spring.order.service.IOrderService;

@Service
public class OrderServiceImpl implements IOrderService {
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
		} else if ( OrderStatus.REJECT == orderPayment.getStatus() && OrderStatus.REJECT == orderInventory.getStatus() ) {
			order.setStatus( OrderStatus.REJECT );
		} else if ( OrderStatus.REJECT == orderPayment.getStatus() || OrderStatus.REJECT == orderInventory.getStatus() ) {
			ServiceSource source = OrderStatus.REJECT == orderPayment.getStatus() ? ServiceSource.PAYMENT : ServiceSource.INVENTORY;
			order.setStatus( OrderStatus.ROLLBACK );
			order.setSource( source );
		}
		return order;
	}
}
