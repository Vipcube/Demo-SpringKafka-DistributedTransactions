package org.vipcube.spring.order.service;

import org.vipcube.spring.domain.dto.Order;

public interface IOrderService {
	void generate();

	Order confirm( Order orderPayment, Order orderInventory );
}
