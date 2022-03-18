package org.vipcube.spring.order.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
	@Bean
	public NewTopic orders() {
		return TopicBuilder.name("orders")
				.partitions(1)
				.compact()
				.build();
	}

	@Bean
	public NewTopic paymentTopic() {
		return TopicBuilder.name("payment-orders")
				.partitions(1)
				.compact()
				.build();
	}

	@Bean
	public NewTopic inventoryTopic() {
		return TopicBuilder.name("inventory-orders")
				.partitions(1)
				.compact()
				.build();
	}
}
