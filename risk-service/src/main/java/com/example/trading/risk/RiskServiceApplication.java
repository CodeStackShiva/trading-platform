package com.example.trading.risk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RiskLimits.class)
public class RiskServiceApplication {
    public static void main(String[] args) { SpringApplication.run(RiskServiceApplication.class, args); }
}
