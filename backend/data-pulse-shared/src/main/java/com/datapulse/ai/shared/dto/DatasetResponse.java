package com.datapulse.ai.shared.dto;

import java.time.Instant;
import java.util.UUID;

public record DatasetResponse(
    UUID id,
    UUID workspaceId,
    UUID dataSourceId,
    String name,
    String description,
    Instant createdAt,
    Instant updatedAt
) {}
