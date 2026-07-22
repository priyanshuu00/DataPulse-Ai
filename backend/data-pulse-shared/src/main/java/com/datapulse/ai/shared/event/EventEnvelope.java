package com.datapulse.ai.shared.event;

import java.time.Instant;
import java.util.UUID;

public record EventEnvelope<T>(
    UUID eventId,
    String eventType,
    String eventVersion,
    Instant occurredAt,
    String correlationId,
    String traceId,
    String source,
    T payload
) {
    public static <T> EventEnvelope<T> create(
            String eventType,
            String eventVersion,
            String correlationId,
            String traceId,
            String source,
            T payload
    ) {
        return new EventEnvelope<>(
                UUID.randomUUID(),
                eventType,
                eventVersion,
                Instant.now(),
                correlationId,
                traceId,
                source,
                payload
        );
    }
}
