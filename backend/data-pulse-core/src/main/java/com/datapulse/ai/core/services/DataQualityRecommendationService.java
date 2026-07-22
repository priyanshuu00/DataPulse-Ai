package com.datapulse.ai.core.services;

import com.datapulse.ai.core.domain.DatasetSchema;
import com.datapulse.ai.core.domain.RuleRecommendation;
import com.datapulse.ai.core.ports.AiServicePort;
import com.datapulse.ai.core.ports.DatasetSchemaRepositoryPort;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DataQualityRecommendationService {

    private final DatasetSchemaRepositoryPort schemaRepositoryPort;
    private final DeterministicRuleEngine deterministicRuleEngine;
    private final AiServicePort aiServicePort;

    public DataQualityRecommendationService(DatasetSchemaRepositoryPort schemaRepositoryPort,
                                            DeterministicRuleEngine deterministicRuleEngine,
                                            AiServicePort aiServicePort) {
        this.schemaRepositoryPort = schemaRepositoryPort;
        this.deterministicRuleEngine = deterministicRuleEngine;
        this.aiServicePort = aiServicePort;
    }

    public List<RuleRecommendation> generateRecommendations(UUID versionId) {
        DatasetSchema schema = schemaRepositoryPort.findByVersionId(versionId)
                .orElseThrow(() -> new IllegalArgumentException("No schema found for version: " + versionId));

        // 1. Deterministic Engine runs offline/locally
        List<RuleRecommendation> deterministicRules = deterministicRuleEngine.generateRules(schema);

        List<RuleRecommendation> allRules = new ArrayList<>(deterministicRules);

        // 2. AI Layer enhances recommendations
        try {
            List<RuleRecommendation> aiRules = aiServicePort.enhanceRecommendations(schema, deterministicRules);
            allRules.addAll(aiRules);
        } catch (Exception e) {
            // Log failure but do not crash. Return deterministic rules.
            // Using System.err for brevity in core module (normally inject an SLF4J logger)
            System.err.println("AI Service failed, returning deterministic rules only: " + e.getMessage());
        }

        return allRules;
    }
}
