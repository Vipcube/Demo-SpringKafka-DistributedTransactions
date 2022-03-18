package org.vipcube.spring.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {
	private Long id;
	private Long customerId;
	private Long productId;
	private int productCount;
	private BigDecimal price;
	@Builder.Default
	private OrderStatus status = OrderStatus.NEW;
	private ServiceSource source;
}
