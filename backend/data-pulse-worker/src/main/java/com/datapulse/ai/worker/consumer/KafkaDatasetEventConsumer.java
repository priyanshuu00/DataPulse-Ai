package com.datapulse.ai.worker.consumer;

import com.datapulse.ai.core.services.SparkOrchestrationService;
import com.datapulse.ai.shared.event.DatasetVersionUploadedPayload;
import com.datapulse.ai.shared.event.EventEnvelope;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaDatasetEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaDatasetEventConsumer.class);

    private final SparkOrchestrationService orchestrationService;
    private final ObjectMapper objectMapper;

    public KafkaDatasetEventConsumer(SparkOrchestrationService orchestrationService, ObjectMapper objectMapper) {
        this.orchestrationService = orchestrationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${datapulse.kafka.topics.dataset-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, String> record) {
        try {
            EventEnvelope<DatasetVersionUploadedPayload> event = objectMapper.readValue(
                    record.value(),
                    new TypeReference<>() {}
            );

            log.info("Received event {} for versionId {} [traceId={}, correlationId={}]",
                    event.eventType(), event.payload().versionId(), event.traceId(), event.correlationId());

            if ("DatasetVersionUploaded".equals(event.eventType())) {
                orchestrationService.orchestrateProcessing(event.payload().versionId());
            }

        } catch (Exception e) {
            log.error("Failed to process Kafka message: {}", record.value(), e);
            // In a production system, push to DLQ here.
        }
    }
}
