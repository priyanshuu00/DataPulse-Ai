package com.datapulse.ai.shared.dto;

import java.time.Instant;
import java.util.UUID;

public record DatasetVersionResponse(
    UUID id,
    UUID datasetId,
    String semanticVersion,
    String originalFilename,
    String objectKey,
    String uri,
    Long size,
    String checksum,
    String status,
    Instant createdAt
) {}
