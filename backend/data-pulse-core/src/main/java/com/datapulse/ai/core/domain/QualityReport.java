package com.datapulse.ai.core.domain;

import java.time.Instant;
import java.util.UUID;

public record QualityReport(
    UUID id,
    UUID datasetVersionId,
    DataQualityScore score,
    String summary,
    Instant generatedAt
) {}
