package com.datapulse.ai.core.domain;

import java.util.List;
import java.util.UUID;

public record DatasetSchema(
    UUID id,
    UUID datasetId,
    UUID versionId,
    List<ColumnMetadata> columns
) {}
