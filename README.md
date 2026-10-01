# Trading Platform - Order & Risk Services

Event-driven order pipeline for a financial-services setting.

```
 Client --POST /api/v1/orders--> order-service --(1 tx)--> SQL Server [orders + outbox]
                                      |  outbox relay
                                      v
                              Kafka: orders.placed ---> risk-service (rules engine)
                                      ^                         |
                      order-service <-+--- Kafka: risk.results <-+
                      (status -> APPROVED / REJECTED)
```

## Run it
```bash
mvn verify                      # build + unit tests (Java 17)
docker compose up --build       # SQL Server, Kafka (KRaft), both services, Prometheus, Grafana

curl -i -X POST localhost:8080/api/v1/orders \
  -H 'Content-Type: application/json' -H 'Idempotency-Key: demo-1' \
  -d '{"clientId":"c1","symbol":"AAPL","side":"BUY","quantity":100,"price":150.25}'
curl localhost:8080/api/v1/orders/<id>          # NEW -> APPROVED within ms
# Reject path: symbol LEH, or quantity 20000, or notional > 1,000,000
```
Prometheus: http://localhost:9090 - Grafana: http://localhost:3000 (Prometheus datasource pre-provisioned).

## Requirement -> where it lives
| Requirement | Evidence in this repo |
|---|---|
| Java 17, OOP | Records for DTOs/events, enums, encapsulated aggregate (`Order.applyRisk`), small single-purpose classes |
| Spring Boot, REST | `OrderController`, Bean Validation, `ProblemDetail` errors, `202 Accepted` async semantics |
| Microservices | Separate `order-service` / `risk-service`, shared contract module `common`, independent deploy + scale |
| SQL Server | Flyway migrations, filtered index, `UPDLOCK, READPAST` outbox polling, optimistic locking (`@Version`), `DATETIME2`, `DECIMAL(19,4)` money |
| Event-driven / Kafka | Keyed topics (per-order ordering), idempotent producer, `acks=all`, DLT with retries, consumer groups |
| Distributed-systems rigor | Transactional outbox (no dual write), at-least-once + idempotent consumers, `Idempotency-Key` API |
| High throughput / low latency | Producer batching + lz4, batched polling, 6 partitions x 3 consumer threads, HikariCP, async flow, k6 SLO test (`loadtest/`) |
| Docker / K8s / OpenShift | Multi-stage non-root Dockerfiles, `k8s/` Deployment+Service+HPA+probes, ConfigMap/Secret |
| Git / CI/CD | `.github/workflows/ci.yml` (test -> image build; deploy stages noted) |
| Cloud-native | 12-factor config via env, graceful shutdown, health probes, stateless services, HPA |
| Observability | Actuator + Micrometer -> Prometheus -> Grafana; custom metrics `orders.placed`, `risk.evaluation`, `risk.decisions`, `outbox.publish.batch`. Splunk: ship JSON logs via the Splunk OTel/HEC forwarder. Dynatrace: attach OneAgent (no code change) |
| Cloud mapping | AWS: EKS + MSK + RDS for SQL Server + Secrets Manager. Azure: AKS + Event Hubs (Kafka API) + Azure SQL. GCP: GKE + Managed Kafka + Cloud SQL for SQL Server |

## Interview talking points
- **Why outbox?** Writing the DB and Kafka separately can lose or duplicate events on a crash; one local transaction removes that.
- **Delivery semantics:** at-least-once end to end; `Order.applyRisk` ignores replays, so effects are exactly-once.
- **Scaling the relay:** `READPAST` lets N replicas drain disjoint rows; ordering is per order key, which is all that's needed.
- **Failure handling:** poison messages go to `orders.placed.DLT` after 3 retries; the partition never stalls.
- **Next steps:** Testcontainers integration tests, Debezium CDC instead of polling, schema registry (Avro), distributed tracing (OpenTelemetry), mTLS/OAuth2.
