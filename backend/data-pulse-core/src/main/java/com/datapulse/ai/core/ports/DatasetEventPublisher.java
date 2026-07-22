package com.datapulse.ai.core.ports;

import com.datapulse.ai.core.domain.DatasetVersion;

public interface DatasetEventPublisher {
    void publishVersionCreatedEvent(DatasetVersion version, String correlationId, String traceId);
    void publishValidationCompletedEvent(DatasetVersion version, boolean passed, String correlationId, String traceId);
}
