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
    private final com.datapulse.ai.core.services.DatasetIngestionService ingestionService;
    private final com.datapulse.ai.core.services.DataQualityRecommendationService recommendationService;
    private final com.datapulse.ai.core.ports.DatasetSchemaRepositoryPort schemaRepositoryPort;

    public DatasetController(DatasetRepository datasetRepository,
                             com.datapulse.ai.core.services.DatasetIngestionService ingestionService,
                             com.datapulse.ai.core.services.DataQualityRecommendationService recommendationService,
                             com.datapulse.ai.core.ports.DatasetSchemaRepositoryPort schemaRepositoryPort) {
        this.datasetRepository = datasetRepository;
        this.ingestionService = ingestionService;
        this.recommendationService = recommendationService;
        this.schemaRepositoryPort = schemaRepositoryPort;
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
    public ResponseEntity<DatasetResponse> getDataset(@PathVariable("id") UUID id) {
        Dataset dataset = datasetRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Dataset not found: " + id));
        return ResponseEntity.ok(mapToResponse(dataset));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DatasetResponse> updateDataset(@PathVariable("id") UUID id, @RequestBody DatasetUpdateRequest request) {
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
    public ResponseEntity<Void> deleteDataset(@PathVariable("id") UUID id) {
        if (datasetRepository.findById(id).isEmpty()) {
            throw new RuntimeException("Dataset not found: " + id);
        }
        datasetRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/versions")
    public ResponseEntity<com.datapulse.ai.shared.dto.DatasetVersionResponse> uploadDataset(
            @PathVariable("id") UUID id,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            @RequestHeader(value = "X-Correlation-ID", required = false) String headerCorrelationId,
            @RequestHeader(value = "X-Trace-ID", required = false) String headerTraceId) {
        
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

            com.datapulse.ai.shared.dto.DatasetVersionResponse response = mapToVersionResponse(version);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .header("X-Correlation-ID", correlationId)
                    .header("X-Trace-ID", traceId)
                    .body(response);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to read upload stream", e);
        }
    }
    
    @GetMapping("/{id}/versions")
    public ResponseEntity<List<com.datapulse.ai.shared.dto.DatasetVersionResponse>> getDatasetVersions(@PathVariable("id") UUID id) {
        List<com.datapulse.ai.shared.dto.DatasetVersionResponse> versions = datasetRepository.findVersionsByDatasetId(id).stream()
                .map(this::mapToVersionResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(versions);
    }

    @GetMapping("/{id}/versions/{versionId}")
    public ResponseEntity<com.datapulse.ai.shared.dto.DatasetVersionResponse> getDatasetVersion(
            @PathVariable("id") UUID id,
            @PathVariable("versionId") UUID versionId) {
        com.datapulse.ai.core.domain.DatasetVersion version = datasetRepository.findVersionById(versionId)
                .orElseThrow(() -> new RuntimeException("Dataset version not found: " + versionId));
        return ResponseEntity.ok(mapToVersionResponse(version));
    }

    @GetMapping({"/{id}/schema", "/{id}/versions/{versionId}/schema"})
    public ResponseEntity<Object> getDatasetSchema(
            @PathVariable("id") UUID id,
            @PathVariable(value = "versionId", required = false) UUID versionId) {
        if (versionId != null) {
            return schemaRepositoryPort.findByVersionId(versionId)
                    .<ResponseEntity<Object>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }
        List<com.datapulse.ai.core.domain.DatasetVersion> versions = datasetRepository.findVersionsByDatasetId(id);
        if (!versions.isEmpty()) {
            UUID latestVersionId = versions.get(versions.size() - 1).id();
            return schemaRepositoryPort.findByVersionId(latestVersionId)
                    .<ResponseEntity<Object>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }
        return ResponseEntity.ok(java.util.Map.of());
    }

    @GetMapping("/{id}/quality")
    public ResponseEntity<Object> getDatasetQuality(@PathVariable("id") UUID id) {
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

    private com.datapulse.ai.shared.dto.DatasetVersionResponse mapToVersionResponse(com.datapulse.ai.core.domain.DatasetVersion version) {
        return new com.datapulse.ai.shared.dto.DatasetVersionResponse(
            version.id(),
            version.datasetId(),
            version.semanticVersion(),
            version.originalFilename(),
            version.storageObject() != null ? version.storageObject().objectKey() : null,
            version.storageObject() != null ? version.storageObject().uri() : null,
            version.storageObject() != null ? version.storageObject().size() : null,
            version.storageObject() != null ? version.storageObject().checksum() : null,
            version.status() != null ? version.status().name() : "UPLOADED",
            version.createdAt()
        );
    }

    @GetMapping("/{id}/versions/{versionId}/recommendations")
    public ResponseEntity<java.util.List<com.datapulse.ai.shared.dto.RuleRecommendationResponse>> getRecommendations(
            @PathVariable("id") UUID id,
            @PathVariable("versionId") UUID versionId) {

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
