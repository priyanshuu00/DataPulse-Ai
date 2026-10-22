package com.datapulse.ai.shared.dto;

public record QualitySummaryResponse(
    long totalRules,
    long activeRules,
    double averageCompliance,
    long schemaViolations,
    String rulesSummary
) {}
