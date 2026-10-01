package com.example.trading.order;

import com.example.trading.common.OrderPlacedEvent;
import com.example.trading.common.Topics;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final OrderRepository orders;
    private final OutboxRepository outbox;
    private final ObjectMapper mapper;
    private final Counter placed;

    public OrderService(OrderRepository orders, OutboxRepository outbox, ObjectMapper mapper, MeterRegistry registry) {
        this.orders = orders; this.outbox = outbox; this.mapper = mapper;
        this.placed = registry.counter("orders.placed");
    }

    /** Order row + outbox row commit atomically (transactional outbox) - no dual-write problem. */
    @Transactional
    public Order place(String idempotencyKey, PlaceOrderRequest req) {
        return orders.findByIdempotencyKey(idempotencyKey).orElseGet(() -> create(idempotencyKey, req));
    }

    private Order create(String key, PlaceOrderRequest req) {
        Order o = orders.save(new Order(key, req));
        var event = new OrderPlacedEvent(o.getId(), o.getClientId(), o.getSymbol(), o.getSide(),
                o.getQuantity(), o.getPrice(), o.getCreatedAt());
        try {
            outbox.save(new OutboxEvent(Topics.ORDERS_PLACED, o.getId().toString(), mapper.writeValueAsString(event)));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
        placed.increment();
        return o;
    }

    @Transactional(readOnly = true)
    public java.util.Optional<Order> find(java.util.UUID id) { return orders.findById(id); }
}
