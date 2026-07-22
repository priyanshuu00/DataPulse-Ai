package com.datapulse.ai.core.domain;

import java.time.Instant;
import java.util.UUID;

public record Dataset(
    UUID id,
    UUID workspaceId,
    UUID dataSourceId,
    String name,
    String description,
    Instant createdAt,
    Instant updatedAt
) {}
