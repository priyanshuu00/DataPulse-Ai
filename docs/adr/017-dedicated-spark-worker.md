# ADR 017: Dedicated Spark Worker and Event-Driven Processing

## Status
Accepted

## Context
When processing large datasets (e.g., inferring schemas, calculating statistics) using Apache Spark, the operations are highly CPU and memory intensive. Running these operations synchronously within the main API application could lead to resource starvation, long HTTP response times, and potential Out-Of-Memory (OOM) crashes affecting all users.

## Decision
1. **Separation of Concerns**: We extract data processing into a standalone Maven module `data-pulse-worker`.
2. **Kafka as the Backbone**: The API continues to publish `DatasetVersionUploaded` events to Kafka. The `data-pulse-worker` consumes these events independently.
3. **Shared SparkSession**: The worker initializes a single, shared `SparkSession` at startup, reusing the JVM context for all subsequent processing rather than incurring the heavy cost of creating a context per message.

## Consequences
- The API layer remains incredibly lightweight and highly responsive.
- The `data-pulse-worker` can be scaled horizontally independent of the API (e.g., deploying more worker instances on memory-optimized instances).
- If a malformed dataset crashes the worker, the API is unaffected and Kafka retains the message for debugging or reprocessing.
- Future deployment strategy easily supports moving from embedded `local[*]` Spark to a true distributed Spark cluster (Standalone, YARN, or Kubernetes) by simply updating the worker's configuration.
