package org.vipcube.spring.payment.service;

import org.vipcube.spring.domain.dto.Order;

public interface IOrderPaymentService {
	void confirm( Order order );
	void reserve( Order order );
}
