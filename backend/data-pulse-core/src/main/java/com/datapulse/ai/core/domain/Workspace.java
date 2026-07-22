package com.datapulse.ai.core.domain;

import java.util.UUID;

public record Workspace(
    UUID id,
    UUID organizationId,
    String name,
    String description
) {}
