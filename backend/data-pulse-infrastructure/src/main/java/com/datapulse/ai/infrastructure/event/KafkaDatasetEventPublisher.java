package com.datapulse.ai.infrastructure.event;

import com.datapulse.ai.core.domain.DatasetVersion;
import com.datapulse.ai.core.ports.DatasetEventPublisher;
import com.datapulse.ai.shared.event.DatasetVersionUploadedPayload;
import com.datapulse.ai.shared.event.EventEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaDatasetEventPublisher implements DatasetEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaDatasetEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String datasetEventsTopic;

    public KafkaDatasetEventPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                                      @Value("${datapulse.kafka.topics.dataset-events}") String datasetEventsTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.datasetEventsTopic = datasetEventsTopic;
    }

    @Override
    public void publishVersionCreatedEvent(DatasetVersion version, String correlationId, String traceId) {
        DatasetVersionUploadedPayload payload = new DatasetVersionUploadedPayload(
                version.datasetId(),
                version.id(),
                // In phase 5 we didn't add workspaceId back to version, so we'll mock it or get it from dataset if we had it.
                // Assuming workspaceId is needed, we could pass it or load it. For now, we'll put a placeholder or null if it's not strictly available.
                null, 
                version.storageObject().objectKey(),
                version.storageObject().checksum()
        );

        EventEnvelope<DatasetVersionUploadedPayload> event = EventEnvelope.create(
                "DatasetVersionUploaded",
                "1.0",
                correlationId,
                traceId,
                "data-pulse-api",
                payload
        );

        String partitionKey = version.datasetId().toString();

        long startTime = System.currentTimeMillis();
        kafkaTemplate.send(datasetEventsTopic, partitionKey, event)
                .whenComplete((result, ex) -> {
                    long duration = System.currentTimeMillis() - startTime;
                    if (ex == null) {
                        log.info("Successfully published event {} to topic {} partition {} [correlationId={}, traceId={}, duration={}ms]",
                                event.eventId(), datasetEventsTopic, result.getRecordMetadata().partition(), correlationId, traceId, duration);
                    } else {
                        log.error("Failed to publish event {} [correlationId={}, traceId={}]", event.eventId(), correlationId, traceId, ex);
                        // In a real outbox pattern, this failure would be recorded to the database or DLQ.
                    }
                });
    }

    @Override
    public void publishValidationCompletedEvent(DatasetVersion version, boolean passed, String correlationId, String traceId) {
        // Implementation for later phase
    }
}
