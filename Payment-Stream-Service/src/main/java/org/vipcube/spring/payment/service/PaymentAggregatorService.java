package org.vipcube.spring.payment.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.Aggregator;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.vipcube.spring.domain.dto.Order;
import org.vipcube.spring.domain.dto.OrderStatus;
import org.vipcube.spring.domain.dto.ServiceSource;
import org.vipcube.spring.payment.entity.CustomerFund;

@Slf4j
@Service
public class PaymentAggregatorService implements Aggregator<Long, Order, CustomerFund> {
	private final KafkaTemplate<Long, Order> template;

	public PaymentAggregatorService( KafkaTemplate<Long, Order> template ){
		this.template = template;
	}

	@Override
	public CustomerFund apply( Long aLong, Order order, CustomerFund customerFund ) {
		switch (order.getStatus()) {
			case CONFIRMED:
				customerFund.setAmountReserved( customerFund.getAmountReserved().subtract( order.getPrice() ) );
		case ROLLBACK:
			if ( ServiceSource.PAYMENT != order.getSource() ){
				customerFund.setAmountReserved( customerFund.getAmountReserved().subtract( order.getPrice() ) );
				customerFund.setAmountAvailable( customerFund.getAmountAvailable().add( order.getPrice() ) );
			}
		case NEW:
			if ( order.getPrice().compareTo( customerFund.getAmountAvailable() ) < 0 ){
				order.setStatus( OrderStatus.ACCEPT );
				customerFund.setAmountReserved( customerFund.getAmountReserved().add( order.getPrice() ) );
				customerFund.setAmountReserved( customerFund.getAmountAvailable().subtract( order.getPrice() ) );
			} else {
				order.setStatus( OrderStatus.REJECT );
			}
			this.template.send( "payment-orders", order.getId(), order );
			log.info( "PaymentAggregatorService: Send customer order: {}", order );
		}
		return customerFund;
	}
}
