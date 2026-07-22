# ADR 015: Event Envelopes, Partition Keys, and Schema Versioning

## Status
Accepted

## Context
When adopting an Event-Driven Architecture with Kafka, simply publishing raw domain JSON payloads leads to a tightly coupled system. If the payload structure changes, consumers break. Furthermore, without deterministic routing, related events can be processed out-of-order by parallel consumers, leading to race conditions.

## Decision
1. **Event Envelope**: All events published to Kafka must be wrapped in a standard `EventEnvelope` containing metadata like `eventId`, `eventType`, `eventVersion`, `correlationId`, and `traceId`.
2. **Event Versioning**: The `eventVersion` explicitly declares the schema version (e.g., `1.0`). Breaking changes to payloads require a bump in this version.
3. **Partition Key**: All events related to a specific dataset will be published using the `datasetId` as the Kafka message key.

## Consequences
- Guaranteed message ordering at the dataset level, preventing race conditions (e.g., a "Validated" event being processed before an "Uploaded" event).
- Standardized deserialization for all platform microservices via the `EventEnvelope`.
- Unified distributed tracing and observability via correlation/trace IDs flowing from the REST API to Kafka consumers.
