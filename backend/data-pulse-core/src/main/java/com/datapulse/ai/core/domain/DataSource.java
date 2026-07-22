package com.datapulse.ai.core.domain;

import java.util.UUID;
import java.util.Map;

public record DataSource(
    UUID id,
    UUID workspaceId,
    String name,
    String type,
    Map<String, String> connectionDetails
) {}
