package org.vipcube.spring.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {
	@Null( message = "id should be empty" )
	private Long id;
	@NotNull( message = "customer id should not empty" )
	private Long customerId;
	@NotNull( message = "product id should not empty" )
	private Long productId;
	private int productCount;
	@NotNull( message = "price should not empty" )
	private BigDecimal price;
	@Builder.Default
	private OrderStatus status = OrderStatus.NEW;
	@Null
	private ServiceSource source;
}
