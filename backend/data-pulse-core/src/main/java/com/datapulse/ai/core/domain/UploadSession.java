package com.datapulse.ai.core.domain;

import java.time.Instant;
import java.util.UUID;

public record UploadSession(
    UUID id,
    UUID datasetId,
    String status,
    long bytesUploaded,
    Instant startTime,
    Instant endTime
) {}
