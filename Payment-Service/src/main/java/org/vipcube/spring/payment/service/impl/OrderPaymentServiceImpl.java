package org.vipcube.spring.payment.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.vipcube.spring.domain.dto.Order;
import org.vipcube.spring.domain.dto.OrderStatus;
import org.vipcube.spring.domain.dto.ServiceSource;
import org.vipcube.spring.payment.entity.CustomerFund;
import org.vipcube.spring.payment.repository.CustomerFundRepository;
import org.vipcube.spring.payment.service.IOrderPaymentService;

@Slf4j
@Service
public class OrderPaymentServiceImpl implements IOrderPaymentService {
	private final CustomerFundRepository repository;
	private final KafkaTemplate<Long, Order> template;

	public OrderPaymentServiceImpl( CustomerFundRepository repository, KafkaTemplate<Long, Order> template ){
		this.repository = repository;
		this.template = template;
	}

	@Override
	public void confirm( Order order ) {
		CustomerFund customerFund = this.repository.findById( order.getCustomerId() ).orElseThrow();
		log.info( "OrderPaymentService: Found customer fund: {}", customerFund );
		if ( OrderStatus.CONFIRMED == order.getStatus() ){
			customerFund.setAmountReserved( customerFund.getAmountReserved().subtract( order.getPrice() ) );
			this.repository.save( customerFund );
		} else if ( OrderStatus.ROLLBACK == order.getStatus() && ServiceSource.PAYMENT != order.getSource() ){
			customerFund.setAmountReserved( customerFund.getAmountReserved().subtract( order.getPrice() ) );
			customerFund.setAmountAvailable( customerFund.getAmountAvailable().add( order.getPrice() ) );
			this.repository.save( customerFund );
		}
	}

	@Override
	public void reserve( Order order ) {
		CustomerFund customerFund = this.repository.findById( order.getCustomerId() ).orElseThrow();
		log.info( "OrderPaymentService: Found customer fund: {}", customerFund );

		order.setSource( ServiceSource.PAYMENT );
		if ( order.getPrice().compareTo( customerFund.getAmountAvailable() ) < 0 ){
			order.setStatus( OrderStatus.ACCEPT );
			customerFund.setAmountReserved( customerFund.getAmountReserved().add( order.getPrice() ) );
			customerFund.setAmountReserved( customerFund.getAmountAvailable().subtract( order.getPrice() ) );
		} else {
			order.setStatus( OrderStatus.REJECT );
		}
		this.repository.save( customerFund );
		this.template.send( "payment-orders", order.getId(), order );
		log.info( "OrderPaymentService: Send customer order: {}", order );
	}
}
