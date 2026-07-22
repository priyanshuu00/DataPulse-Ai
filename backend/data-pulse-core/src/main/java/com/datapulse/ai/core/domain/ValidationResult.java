package com.datapulse.ai.core.domain;

import java.util.List;
import java.util.UUID;

public record ValidationResult(
    UUID id,
    UUID datasetVersionId,
    boolean passed,
    List<String> errors,
    List<String> warnings
) {}
