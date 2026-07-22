package com.datapulse.ai.core.services;

import com.datapulse.ai.core.domain.ColumnMetadata;
import com.datapulse.ai.core.domain.DataQualityRule;
import com.datapulse.ai.core.domain.DatasetSchema;
import com.datapulse.ai.core.domain.RecommendationSource;
import com.datapulse.ai.core.domain.RuleRecommendation;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class DeterministicRuleEngine {

    public List<RuleRecommendation> generateRules(DatasetSchema schema) {
        if (schema == null || schema.columns() == null) {
            return Collections.emptyList();
        }

        List<RuleRecommendation> recommendations = new ArrayList<>();

        for (ColumnMetadata col : schema.columns()) {
            String name = col.name().toLowerCase();
            
            // Unique ID Rule
            if (name.equals("id") || name.endsWith("_id")) {
                recommendations.add(createRecommendation(
                        col.name(), 
                        "UNIQUE", 
                        "Column appears to be an identifier. It should be unique.", 
                        0.95
                ));
            }
            
            // Email Rule
            if (name.contains("email")) {
                recommendations.add(createRecommendation(
                        col.name(), 
                        "REGEX", 
                        "Column appears to contain email addresses. Use standard email regex format.", 
                        0.90
                ));
            }

            // Not Null Rule
            if (!col.isNullable() || (col.nullCount() != null && col.nullCount() == 0)) {
                 recommendations.add(createRecommendation(
                        col.name(), 
                        "NOT_NULL", 
                        "Column has no null values. It should likely remain NOT NULL.", 
                        0.85
                ));
            }
        }

        return recommendations;
    }

    private RuleRecommendation createRecommendation(String column, String ruleType, String explanation, double confidence) {
        return new RuleRecommendation(
                UUID.randomUUID(),
                new DataQualityRule(column, ruleType, Collections.emptyMap()),
                explanation,
                confidence,
                RecommendationSource.RULE_ENGINE,
                Instant.now()
        );
    }
}
