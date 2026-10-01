package com.example.trading.risk;

import java.math.BigDecimal;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("risk")
public record RiskLimits(BigDecimal maxNotional, long maxQuantity, Set<String> restrictedSymbols) {}
