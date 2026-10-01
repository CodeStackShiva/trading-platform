package com.example.trading.common;

import java.util.UUID;

public record RiskResultEvent(UUID orderId, boolean approved, String reason) {}
