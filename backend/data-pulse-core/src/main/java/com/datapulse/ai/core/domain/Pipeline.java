package com.datapulse.ai.core.domain;

import java.util.UUID;

public record Pipeline(
    UUID id,
    UUID workspaceId,
    String name,
    String type,
    String configurationJson
) {}
