package com.example.trading.common;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderPlacedEvent(UUID orderId, String clientId, String symbol, Side side,
                               long quantity, BigDecimal price, Instant placedAt) {
    public BigDecimal notional() { return price.multiply(BigDecimal.valueOf(quantity)); }
}
