package com.example.trading.risk;

import com.example.trading.common.OrderPlacedEvent;
import com.example.trading.common.RiskResultEvent;
import org.springframework.stereotype.Component;

/** Pure, stateless rules: trivially unit-testable and safe to run on any number of replicas. */
@Component
public class RiskEngine {
    private final RiskLimits limits;
    public RiskEngine(RiskLimits limits) { this.limits = limits; }

    public RiskResultEvent evaluate(OrderPlacedEvent o) {
        if (limits.restrictedSymbols().contains(o.symbol()))
            return reject(o, "Restricted symbol " + o.symbol());
        if (o.quantity() > limits.maxQuantity())
            return reject(o, "Quantity exceeds limit " + limits.maxQuantity());
        if (o.notional().compareTo(limits.maxNotional()) > 0)
            return reject(o, "Notional exceeds limit " + limits.maxNotional());
        return new RiskResultEvent(o.orderId(), true, null);
    }

    private RiskResultEvent reject(OrderPlacedEvent o, String reason) {
        return new RiskResultEvent(o.orderId(), false, reason);
    }
}
