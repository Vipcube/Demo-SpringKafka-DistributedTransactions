package org.vipcube.spring.inventory.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {
	@Builder.Default
	private int availableItems = 0;
	@Builder.Default
	private int reservedItems = 0;
}
