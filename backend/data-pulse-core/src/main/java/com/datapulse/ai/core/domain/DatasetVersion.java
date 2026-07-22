package com.datapulse.ai.core.domain;

import java.time.Instant;
import java.util.UUID;

public record DatasetVersion(
    UUID id,
    UUID datasetId,
    String semanticVersion,
    UUID schemaId,
    String originalFilename,
    StorageObject storageObject,
    Long rowCount,
    VersionStatus status,
    String createdBy,
    Instant createdAt,
    Instant updatedAt
) {}
