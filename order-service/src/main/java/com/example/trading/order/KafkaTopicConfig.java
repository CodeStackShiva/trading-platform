package com.example.trading.order;

import com.example.trading.common.Topics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    // Partition count = max consumer parallelism. Keyed by orderId => per-order ordering.
    @Bean NewTopic ordersPlaced() { return TopicBuilder.name(Topics.ORDERS_PLACED).partitions(6).replicas(1).build(); }
    @Bean NewTopic ordersPlacedDlt() { return TopicBuilder.name(Topics.ORDERS_PLACED + ".DLT").partitions(6).replicas(1).build(); }
    @Bean NewTopic riskResults() { return TopicBuilder.name(Topics.RISK_RESULTS).partitions(6).replicas(1).build(); }
}
