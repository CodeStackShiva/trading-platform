package com.example.trading.risk;

import static org.junit.jupiter.api.Assertions.*;

import com.example.trading.common.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class RiskEngineTest {
    private final RiskEngine engine = new RiskEngine(new RiskLimits(new BigDecimal("1000000"), 10_000, Set.of("LEH")));

    private OrderPlacedEvent order(String sym, long qty, String px) {
        return new OrderPlacedEvent(UUID.randomUUID(), "c1", sym, Side.BUY, qty, new BigDecimal(px), Instant.now());
    }

    @Test void approvesWithinLimits() { assertTrue(engine.evaluate(order("AAPL", 100, "150")).approved()); }
    @Test void rejectsRestrictedSymbol() { assertFalse(engine.evaluate(order("LEH", 1, "1")).approved()); }
    @Test void rejectsOversizedQuantity() { assertFalse(engine.evaluate(order("AAPL", 20_000, "1")).approved()); }
    @Test void rejectsNotionalBreach() {
        var r = engine.evaluate(order("AAPL", 9_000, "500"));
        assertFalse(r.approved());
        assertTrue(r.reason().contains("Notional"));
    }
}
