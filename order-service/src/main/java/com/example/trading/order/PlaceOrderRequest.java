package com.example.trading.order;

import com.example.trading.common.Side;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record PlaceOrderRequest(
        @NotBlank String clientId,
        @NotBlank @Size(max = 12) String symbol,
        @NotNull Side side,
        @Positive long quantity,
        @NotNull @Positive BigDecimal price) {}
