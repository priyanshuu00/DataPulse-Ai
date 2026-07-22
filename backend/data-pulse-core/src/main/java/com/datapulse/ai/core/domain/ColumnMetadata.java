package com.datapulse.ai.core.domain;

import java.util.UUID;

public record ColumnMetadata(
    UUID id,
    String name,
    String dataType,
    boolean isNullable,
    String description,
    Long nullCount,
    String sampleValues
) {}
