package com.example.trading.order;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Relays committed outbox rows to Kafka. At-least-once; consumers are idempotent. */
@Component
public class OutboxPublisher {
    private final OutboxRepository outbox;
    private final KafkaTemplate<String, String> kafka;
    private final Timer batchTimer;

    public OutboxPublisher(OutboxRepository outbox, KafkaTemplate<String, String> kafka, MeterRegistry registry) {
        this.outbox = outbox; this.kafka = kafka;
        this.batchTimer = registry.timer("outbox.publish.batch");
    }

    @Scheduled(fixedDelayString = "${outbox.poll-ms:200}")
    @Transactional
    public void publish() {
        List<OutboxEvent> batch = outbox.lockNextBatch();
        if (batch.isEmpty()) return;
        batchTimer.record(() -> {
            List<CompletableFuture<?>> sends = new ArrayList<>(batch.size());
            for (OutboxEvent e : batch) sends.add(kafka.send(e.getTopic(), e.getMessageKey(), e.getPayload()));
            sends.forEach(CompletableFuture::join);   // fail the tx (rows stay unpublished) if any send fails
            batch.forEach(OutboxEvent::markPublished);
        });
    }
}
