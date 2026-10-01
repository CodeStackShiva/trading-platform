package com.example.trading.order;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {
    /** UPDLOCK+READPAST lets several replicas drain the outbox concurrently without double-publishing. */
    @Query(value = "SELECT TOP 500 * FROM outbox WITH (UPDLOCK, READPAST, ROWLOCK) WHERE published = 0 ORDER BY id",
           nativeQuery = true)
    List<OutboxEvent> lockNextBatch();
}
