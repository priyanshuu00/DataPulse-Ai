package com.datapulse.ai.core.domain;

import java.util.UUID;

public record Organization(
    UUID id,
    String name,
    String domain
) {}
