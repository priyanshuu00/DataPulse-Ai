package com.datapulse.ai.core.domain;

import java.time.Instant;
import java.util.UUID;

public record PipelineRun(
    UUID id,
    UUID pipelineId,
    String status,
    Instant startTime,
    Instant endTime,
    String logsUrl
) {}
