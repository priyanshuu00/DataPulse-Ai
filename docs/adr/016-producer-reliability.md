# ADR 016: Producer Reliability and The Transactional Outbox Pattern

## Status
Accepted

## Context
In our ingestion flow, we save `DatasetVersion` metadata to PostgreSQL and immediately publish a `DatasetVersionUploaded` event to Kafka. This is a "dual-write" scenario. If the database transaction commits but Kafka is temporarily unavailable, the event is permanently lost, leaving the downstream system permanently unaware of the new data.

## Decision
1. **Producer Reliability**: The Kafka producer is strictly configured with `acks=all`, infinite retries, and `enable.idempotence=true`.
2. **Outbox Pattern Preparation**: We acknowledge that a simple `kafkaTemplate.send()` after a database save is theoretically vulnerable to dual-write failure. The long-term architecture mandates the **Transactional Outbox Pattern** (e.g., using Debezium or a polling service to read an `outbox` table updated in the same transaction as the dataset metadata).

## Consequences
- For Phase 6, the system relies on high-reliability producer settings, which reduces the chance of message loss significantly but does not eliminate it entirely in edge cases (e.g., sudden application crash between DB commit and Kafka send).
- The infrastructure is explicitly documented to evolve toward a CDC/Outbox implementation as the platform matures, ensuring zero-data-loss guarantees.
