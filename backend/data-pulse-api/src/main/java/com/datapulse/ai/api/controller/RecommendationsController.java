package com.datapulse.ai.api.controller;

import com.datapulse.ai.core.domain.DatasetVersion;
import com.datapulse.ai.core.domain.RuleRecommendation;
import com.datapulse.ai.core.ports.DatasetRepository;
import com.datapulse.ai.core.ports.DatasetSchemaRepositoryPort;
import com.datapulse.ai.core.services.DataQualityRecommendationService;
import com.datapulse.ai.shared.dto.RuleRecommendationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/recommendations")
public class RecommendationsController {

    private final DatasetRepository datasetRepository;
    private final DatasetSchemaRepositoryPort schemaRepositoryPort;
    private final DataQualityRecommendationService recommendationService;

    public RecommendationsController(
            DatasetRepository datasetRepository,
            DatasetSchemaRepositoryPort schemaRepositoryPort,
            DataQualityRecommendationService recommendationService) {
        this.datasetRepository = datasetRepository;
        this.schemaRepositoryPort = schemaRepositoryPort;
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public ResponseEntity<List<RuleRecommendationResponse>> getAllRecommendations() {
        List<DatasetVersion> allVersions = datasetRepository.findAllVersions();
        List<RuleRecommendationResponse> responses = new ArrayList<>();

        for (DatasetVersion v : allVersions) {
            if (v.schemaId() != null || schemaRepositoryPort.findByVersionId(v.id()).isPresent()) {
                try {
                    List<RuleRecommendation> rules = recommendationService.generateRecommendations(v.id());
                    for (RuleRecommendation r : rules) {
                        responses.add(new RuleRecommendationResponse(
                                r.id(),
                                r.rule().columnName(),
                                r.rule().ruleType(),
                                r.rule().parameters(),
                                r.explanation(),
                                r.confidenceScore(),
                                r.source().name(),
                                r.generatedAt()
                        ));
                    }
                } catch (Exception ignored) {
                }
            }
        }

        return ResponseEntity.ok(responses);
    }
}
