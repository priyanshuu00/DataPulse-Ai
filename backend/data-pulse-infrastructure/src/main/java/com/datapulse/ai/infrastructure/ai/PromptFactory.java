package com.datapulse.ai.infrastructure.ai;

import com.datapulse.ai.core.domain.ColumnMetadata;
import com.datapulse.ai.core.domain.DatasetSchema;
import com.datapulse.ai.core.domain.RuleRecommendation;

import java.util.List;
import java.util.stream.Collectors;

public class PromptFactory {

    public String buildPrompt(DatasetSchema schema, List<RuleRecommendation> existingRules) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an expert Data Engineer and AI Data Quality Assistant.\n");
        prompt.append("Analyze the following dataset schema metadata and recommend advanced data quality rules.\n\n");
        
        prompt.append("### SCHEMA METADATA ###\n");
        for (ColumnMetadata col : schema.columns()) {
            prompt.append(String.format("- Column: '%s' | Type: %s | Nullable: %s | Null Count: %s\n",
                    col.name(), col.dataType(), col.isNullable(), 
                    col.nullCount() != null ? col.nullCount() : "Unknown"));
        }

        prompt.append("\n### EXISTING DETERMINISTIC RULES ###\n");
        if (existingRules.isEmpty()) {
            prompt.append("None.\n");
        } else {
            for (RuleRecommendation rule : existingRules) {
                prompt.append(String.format("- Column: '%s', Rule: %s\n", 
                        rule.rule().columnName(), rule.rule().ruleType()));
            }
        }

        prompt.append("\n### INSTRUCTIONS ###\n");
        prompt.append("1. Do not repeat existing deterministic rules.\n");
        prompt.append("2. Suggest business constraints (e.g., age > 0, status in ['ACTIVE', 'INACTIVE']).\n");
        prompt.append("3. Return ONLY a valid JSON array of objects with the following schema:\n");
        prompt.append("[ { \"columnName\": \"string\", \"ruleType\": \"string\", \"parameters\": { \"key\": \"value\" }, \"explanation\": \"string\", \"confidenceScore\": 0.95 } ]\n");

        return prompt.toString();
    }
}
