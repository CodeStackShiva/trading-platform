package com.example.trading.order;

import com.example.trading.common.RiskResultEvent;
import com.example.trading.common.Topics;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RiskResultListener {
    private final OrderRepository orders;
    private final ObjectMapper mapper;
    public RiskResultListener(OrderRepository orders, ObjectMapper mapper) { this.orders = orders; this.mapper = mapper; }

    @KafkaListener(topics = Topics.RISK_RESULTS, groupId = "order-service", concurrency = "3")
    @Transactional
    public void on(String payload) throws Exception {
        RiskResultEvent r = mapper.readValue(payload, RiskResultEvent.class);
        orders.findById(r.orderId()).ifPresent(o -> o.applyRisk(r.approved(), r.reason()));
    }
}
