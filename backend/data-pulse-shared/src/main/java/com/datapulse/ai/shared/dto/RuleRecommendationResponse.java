package com.datapulse.ai.shared.dto;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record RuleRecommendationResponse(
    UUID id,
    String columnName,
    String ruleType,
    Map<String, String> parameters,
    String explanation,
    double confidenceScore,
    String source,
    Instant generatedAt
) {}
