package com.example.trading.risk;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorConfig {
    /** 3 retries, then park the poison message on <topic>.DLT so the partition never blocks. */
    @Bean
    DefaultErrorHandler errorHandler(KafkaOperations<Object, Object> ops) {
        return new DefaultErrorHandler(new DeadLetterPublishingRecoverer(ops), new FixedBackOff(500, 3));
    }
}
