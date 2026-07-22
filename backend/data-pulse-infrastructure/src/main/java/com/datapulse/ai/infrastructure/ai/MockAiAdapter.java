package com.datapulse.ai.infrastructure.ai;

import com.datapulse.ai.core.domain.DataQualityRule;
import com.datapulse.ai.core.domain.DatasetSchema;
import com.datapulse.ai.core.domain.RecommendationSource;
import com.datapulse.ai.core.domain.RuleRecommendation;
import com.datapulse.ai.core.ports.AiServicePort;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MockAiAdapter implements AiServicePort {

    private static final Logger log = LoggerFactory.getLogger(MockAiAdapter.class);
    private final PromptFactory promptFactory;
    private final ObjectMapper objectMapper;

    public MockAiAdapter(PromptFactory promptFactory, ObjectMapper objectMapper) {
        this.promptFactory = promptFactory;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<RuleRecommendation> enhanceRecommendations(DatasetSchema schema, List<RuleRecommendation> existingRules) {
        String prompt = promptFactory.buildPrompt(schema, existingRules);
        log.info("Generating AI Recommendations with prompt size: {} bytes", prompt.length());

        // Simulate AI processing delay
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        // Mock JSON response from an LLM
        String mockJsonResponse = "[\n" +
                "  {\n" +
                "    \"columnName\": \"age\",\n" +
                "    \"ruleType\": \"NUMERIC_RANGE\",\n" +
                "    \"parameters\": { \"min\": \"0\", \"max\": \"120\" },\n" +
                "    \"explanation\": \"Age must be a positive integer and typically does not exceed 120.\",\n" +
                "    \"confidenceScore\": 0.98\n" +
                "  },\n" +
                "  {\n" +
                "    \"columnName\": \"status\",\n" +
                "    \"ruleType\": \"ALLOWED_VALUES\",\n" +
                "    \"parameters\": { \"values\": \"ACTIVE,INACTIVE,PENDING\" },\n" +
                "    \"explanation\": \"Categorical status column typically has a finite set of allowed states.\",\n" +
                "    \"confidenceScore\": 0.85\n" +
                "  }\n" +
                "]";

        try {
            List<MockAiResponse> responses = objectMapper.readValue(mockJsonResponse, new TypeReference<>() {});
            List<RuleRecommendation> recommendations = new ArrayList<>();
            for (MockAiResponse resp : responses) {
                recommendations.add(new RuleRecommendation(
                        UUID.randomUUID(),
                        new DataQualityRule(resp.columnName(), resp.ruleType(), resp.parameters()),
                        resp.explanation(),
                        resp.confidenceScore(),
                        RecommendationSource.AI,
                        Instant.now()
                ));
            }
            return recommendations;
        } catch (Exception e) {
            log.error("Failed to parse AI JSON response", e);
            return List.of(); // Return empty if AI hallucinated bad JSON
        }
    }

    // DTO for JSON parsing
    private record MockAiResponse(
            String columnName,
            String ruleType,
            Map<String, String> parameters,
            String explanation,
            double confidenceScore
    ) {}
}
