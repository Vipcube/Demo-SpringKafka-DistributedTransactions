package org.vipcube.spring.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerFund implements Serializable {
	@Builder.Default
	private BigDecimal amountAvailable = BigDecimal.ZERO;
	@Builder.Default
	private BigDecimal amountReserved = BigDecimal.ZERO;
}
