package com.datapulse.ai.shared.dto;

import java.time.Instant;
import java.util.UUID;

public record PipelineJobResponse(
    String id,
    UUID datasetId,
    String datasetName,
    String version,
    String stage,
    String kafkaStatus,
    String sparkStatus,
    String duration,
    String topic,
    Instant createdAt
) {}
