package org.vipcube.spring.inventory.service;

import org.vipcube.spring.domain.dto.Order;

public interface IOrderInventoryService {
	void confirm( Order order );
	void reserve( Order order );
}
