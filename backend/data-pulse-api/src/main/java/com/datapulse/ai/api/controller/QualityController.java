package com.datapulse.ai.api.controller;

import com.datapulse.ai.core.domain.DatasetVersion;
import com.datapulse.ai.core.domain.RuleRecommendation;
import com.datapulse.ai.core.ports.DatasetRepository;
import com.datapulse.ai.core.ports.DatasetSchemaRepositoryPort;
import com.datapulse.ai.core.services.DataQualityRecommendationService;
import com.datapulse.ai.shared.dto.QualitySummaryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quality")
public class QualityController {

    private final DatasetRepository datasetRepository;
    private final DatasetSchemaRepositoryPort schemaRepositoryPort;
    private final DataQualityRecommendationService recommendationService;

    public QualityController(
            DatasetRepository datasetRepository,
            DatasetSchemaRepositoryPort schemaRepositoryPort,
            DataQualityRecommendationService recommendationService) {
        this.datasetRepository = datasetRepository;
        this.schemaRepositoryPort = schemaRepositoryPort;
        this.recommendationService = recommendationService;
    }

    @GetMapping("/summary")
    public ResponseEntity<QualitySummaryResponse> getQualitySummary() {
        List<DatasetVersion> allVersions = datasetRepository.findAllVersions();
        long totalRules = 0;
        long totalViolations = 0;
        double totalConfidence = 0.0;
        long count = 0;

        for (DatasetVersion v : allVersions) {
            if (v.schemaId() != null || schemaRepositoryPort.findByVersionId(v.id()).isPresent()) {
                try {
                    List<RuleRecommendation> recs = recommendationService.generateRecommendations(v.id());
                    totalRules += recs.size();
                    for (RuleRecommendation r : recs) {
                        totalConfidence += r.confidenceScore();
                        count++;
                    }
                } catch (Exception ignored) {
                }
            }
        }

        double averageCompliance = count > 0 ? (totalConfidence / count) * 100.0 : 100.0;
        String summary = totalRules > 0 
                ? totalRules + " rules active across cataloged schemas"
                : "No rules active (catalog has no schemas yet)";

        return ResponseEntity.ok(new QualitySummaryResponse(
                totalRules,
                totalRules,
                Math.round(averageCompliance * 10.0) / 10.0,
                totalViolations,
                summary
        ));
    }
}
