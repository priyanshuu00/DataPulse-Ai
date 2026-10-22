package com.datapulse.ai.infrastructure.ai;

import com.datapulse.ai.core.domain.DataQualityRule;
import com.datapulse.ai.core.domain.DatasetSchema;
import com.datapulse.ai.core.domain.RecommendationSource;
import com.datapulse.ai.core.domain.RuleRecommendation;
import com.datapulse.ai.core.ports.AiServicePort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GeminiAiAdapter implements AiServicePort {

    private static final Logger log = LoggerFactory.getLogger(GeminiAiAdapter.class);
    private final PromptFactory promptFactory;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final HttpClient httpClient;
    private final MockAiAdapter fallbackAdapter;

    public GeminiAiAdapter(PromptFactory promptFactory, ObjectMapper objectMapper, String apiKey) {
        this.promptFactory = promptFactory;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.fallbackAdapter = new MockAiAdapter(promptFactory, objectMapper);
    }

    @Override
    public List<RuleRecommendation> enhanceRecommendations(DatasetSchema schema, List<RuleRecommendation> existingRules) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Gemini API Key is missing. Falling back to Mock AI Adapter.");
            return fallbackAdapter.enhanceRecommendations(schema, existingRules);
        }

        try {
            String promptText = promptFactory.buildPrompt(schema, existingRules);
            log.info("Querying Google Gemini AI for schema recommendations...");

            String jsonPayload = objectMapper.writeValueAsString(Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(Map.of("text", promptText)))
                    )
            ));

            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.8-flash:generateContent?key=" + apiKey;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .timeout(Duration.ofSeconds(60))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode rootNode = objectMapper.readTree(response.body());
                String responseText = rootNode
                        .path("candidates").get(0)
                        .path("content").path("parts").get(0)
                        .path("text").asText();

                // Clean markdown code blocks if Gemini returns ```json ... ```
                String cleanJson = responseText.replaceAll("```json", "").replaceAll("```", "").trim();

                List<GeminiRuleDto> rules = objectMapper.readValue(cleanJson, new TypeReference<>() {});
                List<RuleRecommendation> recommendations = new ArrayList<>();

                for (GeminiRuleDto r : rules) {
                    // LLMs sometimes return structured parameter values (arrays, objects,
                    // numbers) — e.g. allowedValues: ["active","inactive"]. The domain
                    // contract is Map<String,String>, so serialize non-string values as JSON.
                    Map<String, String> parameters = new LinkedHashMap<>();
                    if (r.parameters() != null) {
                        r.parameters().forEach((key, value) -> {
                            if (value == null) {
                                parameters.put(key, null);
                            } else if (value instanceof String text) {
                                parameters.put(key, text);
                            } else {
                                try {
                                    parameters.put(key, objectMapper.writeValueAsString(value));
                                } catch (JsonProcessingException e) {
                                    parameters.put(key, String.valueOf(value));
                                }
                            }
                        });
                    }

                    recommendations.add(new RuleRecommendation(
                            UUID.randomUUID(),
                            new DataQualityRule(r.columnName(), r.ruleType(), parameters),
                            r.explanation(),
                            r.confidenceScore(),
                            RecommendationSource.AI,
                            Instant.now()
                    ));
                }

                log.info("Successfully generated {} Gemini AI recommendations", recommendations.size());
                return recommendations;
            } else {
                log.error("Gemini API HTTP Error {}: {}", response.statusCode(), response.body());
            }
        } catch (Exception e) {
            log.error("Failed to query Gemini AI API, falling back to mock adapter: {}", e.getMessage());
        }

        return fallbackAdapter.enhanceRecommendations(schema, existingRules);
    }

    private record GeminiRuleDto(
            String columnName,
            String ruleType,
            Map<String, Object> parameters,
            String explanation,
            double confidenceScore
    ) {}
}
