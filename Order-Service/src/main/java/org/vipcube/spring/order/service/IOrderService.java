package org.vipcube.spring.order.service;

import org.vipcube.spring.domain.dto.Order;

public interface IOrderService {
	Order confirm( Order orderPayment, Order orderInventory );
}
