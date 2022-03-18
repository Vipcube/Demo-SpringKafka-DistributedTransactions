package org.vipcube.spring.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
	private Long id;
	private Long customerId;
	private Long productId;
	private int productCount;
	private BigDecimal price;
	private String status;
	private String source;
}
