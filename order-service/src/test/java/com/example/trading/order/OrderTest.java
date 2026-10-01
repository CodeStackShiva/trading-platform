package com.example.trading.order;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.trading.common.Side;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class OrderTest {
    private Order newOrder() {
        return new Order("k1", new PlaceOrderRequest("c1", "aapl", Side.BUY, 10, new BigDecimal("100")));
    }

    @Test void approvalMovesNewToApproved() {
        Order o = newOrder();
        o.applyRisk(true, null);
        assertEquals(OrderStatus.APPROVED, o.getStatus());
    }

    @Test void finalStateIsNotOverwrittenByDuplicateResult() {
        Order o = newOrder();
        o.applyRisk(false, "limit");
        o.applyRisk(true, null);
        assertEquals(OrderStatus.REJECTED, o.getStatus());
        assertEquals("AAPL", o.getSymbol());
    }
}
