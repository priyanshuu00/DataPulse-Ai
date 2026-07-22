package com.datapulse.ai.core.domain;

import java.util.UUID;

public record ProcessingResult(
    UUID versionId,
    DatasetSchema schema,
    long rowCount,
    long columnCount,
    long fileSize,
    String inferredFormat,
    long durationMs
) {}
