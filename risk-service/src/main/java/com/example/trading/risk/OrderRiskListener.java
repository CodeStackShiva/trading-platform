package com.example.trading.risk;

import com.example.trading.common.OrderPlacedEvent;
import com.example.trading.common.RiskResultEvent;
import com.example.trading.common.Topics;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderRiskListener {
    private final RiskEngine engine;
    private final ObjectMapper mapper;
    private final KafkaTemplate<String, String> kafka;
    private final Timer latency;
    private final MeterRegistry registry;

    public OrderRiskListener(RiskEngine engine, ObjectMapper mapper, KafkaTemplate<String, String> kafka, MeterRegistry registry) {
        this.engine = engine; this.mapper = mapper; this.kafka = kafka; this.registry = registry;
        this.latency = registry.timer("risk.evaluation");
    }

    @KafkaListener(topics = Topics.ORDERS_PLACED, groupId = "risk-service", concurrency = "3")
    public void on(String payload) throws Exception {
        OrderPlacedEvent order = mapper.readValue(payload, OrderPlacedEvent.class);
        RiskResultEvent result = latency.recordCallable(() -> engine.evaluate(order));
        registry.counter("risk.decisions", "approved", String.valueOf(result.approved())).increment();
        kafka.send(Topics.RISK_RESULTS, order.orderId().toString(), mapper.writeValueAsString(result));
    }
}
