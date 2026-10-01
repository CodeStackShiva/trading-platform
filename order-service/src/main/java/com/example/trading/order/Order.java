package com.example.trading.order;

import com.example.trading.common.Side;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {
    @Id private UUID id;
    @Column(name = "idempotency_key", nullable = false, unique = true) private String idempotencyKey;
    private String clientId;
    private String symbol;
    @Enumerated(EnumType.STRING) private Side side;
    private long quantity;
    private BigDecimal price;
    @Enumerated(EnumType.STRING) private OrderStatus status;
    private String rejectReason;
    private Instant createdAt;
    @Version private long version;

    protected Order() {}

    public Order(String idempotencyKey, PlaceOrderRequest r) {
        this.id = UUID.randomUUID();
        this.idempotencyKey = idempotencyKey;
        this.clientId = r.clientId();
        this.symbol = r.symbol().toUpperCase();
        this.side = r.side();
        this.quantity = r.quantity();
        this.price = r.price();
        this.status = OrderStatus.NEW;
        this.createdAt = Instant.now();
    }

    /** Idempotent: duplicate/late risk results never overwrite a final state. */
    public void applyRisk(boolean approved, String reason) {
        if (status != OrderStatus.NEW) return;
        status = approved ? OrderStatus.APPROVED : OrderStatus.REJECTED;
        rejectReason = approved ? null : reason;
    }

    public UUID getId() { return id; }
    public String getClientId() { return clientId; }
    public String getSymbol() { return symbol; }
    public Side getSide() { return side; }
    public long getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }
    public OrderStatus getStatus() { return status; }
    public String getRejectReason() { return rejectReason; }
    public Instant getCreatedAt() { return createdAt; }
}
