package com.datapulse.ai.core.domain;

import java.util.Map;

public record DataQualityRule(
    String columnName,
    String ruleType,
    Map<String, String> parameters
) {}
