package org.vipcube.spring.payment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.vipcube.spring.domain.dto.Order;
import org.vipcube.spring.domain.dto.OrderStatus;

@Slf4j
@Service
public class PaymentProcessService {
	private final IOrderPaymentService orderPaymentService;

	public PaymentProcessService( IOrderPaymentService orderPaymentService ){
		this.orderPaymentService = orderPaymentService;
	}

	@KafkaListener( id = "orders", topics = "orders", groupId = "payment")
	public void onOrder( Order order ) {
		log.info( "PaymentProcessService: Received order: {}" , order );
		if ( OrderStatus.NEW == order.getStatus() ) {
			this.orderPaymentService.reserve( order );
		} else {
			this.orderPaymentService.confirm( order );
		}
	}
}
