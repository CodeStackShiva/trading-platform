package com.example.trading.order;

import com.example.trading.common.Side;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderResponse(UUID id, String clientId, String symbol, Side side, long quantity,
                            BigDecimal price, OrderStatus status, String rejectReason, Instant createdAt) {
    public static OrderResponse from(Order o) {
        return new OrderResponse(o.getId(), o.getClientId(), o.getSymbol(), o.getSide(), o.getQuantity(),
                o.getPrice(), o.getStatus(), o.getRejectReason(), o.getCreatedAt());
    }
}
