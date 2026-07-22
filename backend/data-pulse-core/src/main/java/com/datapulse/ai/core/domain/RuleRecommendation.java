package com.datapulse.ai.core.domain;

import java.time.Instant;
import java.util.UUID;

public record RuleRecommendation(
    UUID id,
    DataQualityRule rule,
    String explanation,
    double confidenceScore,
    RecommendationSource source,
    Instant generatedAt
) {}
