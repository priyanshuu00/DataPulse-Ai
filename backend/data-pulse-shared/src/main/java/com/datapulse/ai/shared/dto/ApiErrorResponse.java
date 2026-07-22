package com.datapulse.ai.shared.dto;

import java.time.Instant;

public record ApiErrorResponse(
    Instant timestamp,
    int status,
    String error,
    String path,
    String traceId
) {}
