package com.datapulse.ai.api.controller;

import com.datapulse.ai.core.domain.Dataset;
import com.datapulse.ai.core.ports.DatasetRepository;
import com.datapulse.ai.shared.dto.DatasetCreateRequest;
import com.datapulse.ai.shared.dto.DatasetResponse;
import com.datapulse.ai.shared.dto.DatasetUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/datasets")
public class DatasetController {

    private final DatasetRepository datasetRepository;

    public DatasetController(DatasetRepository datasetRepository) {
        this.datasetRepository = datasetRepository;
    }

    @PostMapping
    public ResponseEntity<DatasetResponse> createDataset(@Valid @RequestBody DatasetCreateRequest request) {
        Dataset dataset = new Dataset(
            UUID.randomUUID(),
            request.workspaceId(),
            request.dataSourceId(),
            request.name(),
            request.description(),
            Instant.now(),
            Instant.now()
        );
        
        Dataset saved = datasetRepository.save(dataset);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(saved));
    }

    @GetMapping
    public ResponseEntity<List<DatasetResponse>> listDatasets() {
        List<DatasetResponse> responses = datasetRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DatasetResponse> getDataset(@PathVariable UUID id) {
        Dataset dataset = datasetRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Dataset not found: " + id));
        return ResponseEntity.ok(mapToResponse(dataset));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DatasetResponse> updateDataset(@PathVariable UUID id, @RequestBody DatasetUpdateRequest request) {
        Dataset existing = datasetRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Dataset not found: " + id));
            
        Dataset updated = new Dataset(
            existing.id(),
            existing.workspaceId(),
            existing.dataSourceId(),
            request.name() != null ? request.name() : existing.name(),
            request.description() != null ? request.description() : existing.description(),
            existing.createdAt(),
            Instant.now()
        );
        
        Dataset saved = datasetRepository.save(updated);
        return ResponseEntity.ok(mapToResponse(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDataset(@PathVariable UUID id) {
        if (datasetRepository.findById(id).isEmpty()) {
            throw new RuntimeException("Dataset not found: " + id);
        }
        datasetRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/versions")
    public ResponseEntity<com.datapulse.ai.shared.dto.DatasetVersionResponse> uploadDataset(
            @PathVariable UUID id,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            @RequestHeader(value = "X-Correlation-ID", required = false) String headerCorrelationId,
            @RequestHeader(value = "X-Trace-ID", required = false) String headerTraceId,
            com.datapulse.ai.core.services.DatasetIngestionService ingestionService) {
        
        String correlationId = (headerCorrelationId != null && !headerCorrelationId.isBlank()) 
                ? headerCorrelationId : UUID.randomUUID().toString();
        String traceId = (headerTraceId != null && !headerTraceId.isBlank()) 
                ? headerTraceId : UUID.randomUUID().toString();

        try {
            com.datapulse.ai.core.domain.DatasetVersion version = ingestionService.ingestFile(
                    id,
                    file.getOriginalFilename(),
                    file.getInputStream(),
                    file.getContentType(),
                    file.getSize(),
                    correlationId,
                    traceId
            );

            com.datapulse.ai.shared.dto.DatasetVersionResponse response = new com.datapulse.ai.shared.dto.DatasetVersionResponse(
                    version.id(),
                    version.datasetId(),
                    version.semanticVersion(),
                    version.originalFilename(),
                    version.storageObject().objectKey(),
                    version.storageObject().uri(),
                    version.storageObject().size(),
                    version.storageObject().checksum(),
                    version.status().name(),
                    version.createdAt()
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .header("X-Correlation-ID", correlationId)
                    .header("X-Trace-ID", traceId)
                    .body(response);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to read upload stream", e);
        }
    }
    
    @GetMapping("/{id}/versions")
    public ResponseEntity<Object> getDatasetVersions(@PathVariable UUID id) {
        // Placeholder for phase 3, returning empty list
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}/schema")
    public ResponseEntity<Object> getDatasetSchema(@PathVariable UUID id) {
        // Placeholder for phase 3, returning empty object
        return ResponseEntity.ok(java.util.Map.of());
    }

    @GetMapping("/{id}/quality")
    public ResponseEntity<Object> getDatasetQuality(@PathVariable UUID id) {
        // Placeholder for phase 3, returning empty object
        return ResponseEntity.ok(java.util.Map.of());
    }

    private DatasetResponse mapToResponse(Dataset dataset) {
        return new DatasetResponse(
            dataset.id(),
            dataset.workspaceId(),
            dataset.dataSourceId(),
            dataset.name(),
            dataset.description(),
            dataset.createdAt(),
            dataset.updatedAt()
        );
    }
    @GetMapping("/{id}/versions/{versionId}/recommendations")
    public ResponseEntity<java.util.List<com.datapulse.ai.shared.dto.RuleRecommendationResponse>> getRecommendations(
            @PathVariable UUID id,
            @PathVariable UUID versionId,
            com.datapulse.ai.core.services.DataQualityRecommendationService recommendationService) {

        java.util.List<com.datapulse.ai.core.domain.RuleRecommendation> rules = recommendationService.generateRecommendations(versionId);

        java.util.List<com.datapulse.ai.shared.dto.RuleRecommendationResponse> response = rules.stream()
                .map(r -> new com.datapulse.ai.shared.dto.RuleRecommendationResponse(
                        r.id(),
                        r.rule().columnName(),
                        r.rule().ruleType(),
                        r.rule().parameters(),
                        r.explanation(),
                        r.confidenceScore(),
                        r.source().name(),
                        r.generatedAt()
                )).toList();

        return ResponseEntity.ok(response);
    }
}
