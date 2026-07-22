package com.datapulse.ai.core.services;

import com.datapulse.ai.core.domain.DatasetVersion;
import com.datapulse.ai.core.domain.ProcessingResult;
import com.datapulse.ai.core.domain.VersionStatus;
import com.datapulse.ai.core.ports.DataProcessorPort;
import com.datapulse.ai.core.ports.DatasetRepository;

import com.datapulse.ai.core.ports.DatasetSchemaRepositoryPort;

import java.time.Instant;
import java.util.UUID;

public class SparkOrchestrationService {

    private final DataProcessorPort dataProcessorPort;
    private final DatasetRepository datasetRepository;
    private final DatasetSchemaRepositoryPort schemaRepository;

    public SparkOrchestrationService(DataProcessorPort dataProcessorPort, DatasetRepository datasetRepository, DatasetSchemaRepositoryPort schemaRepository) {
        this.dataProcessorPort = dataProcessorPort;
        this.datasetRepository = datasetRepository;
        this.schemaRepository = schemaRepository;
    }

    public void orchestrateProcessing(UUID versionId) {
        DatasetVersion version = datasetRepository.findVersionById(versionId)
                .orElseThrow(() -> new IllegalArgumentException("Version not found: " + versionId));

        if (version.status() != VersionStatus.UPLOADED) {
            // Already processing, processed, or failed
            return; 
        }

        // 1. Transition to PROCESSING
        updateStatus(version, VersionStatus.PROCESSING);

        try {
            // 2. Invoke Spark Processing
            ProcessingResult result = dataProcessorPort.processDataset(
                    version.datasetId(), 
                    version.id(), 
                    version.storageObject()
            );

            // 3. Save schema/metadata and transition to READY
            schemaRepository.save(result.schema());
            
            DatasetVersion readyVersion = new DatasetVersion(
                    version.id(),
                    version.datasetId(),
                    version.semanticVersion(),
                    result.schema().id(),
                    version.originalFilename(),
                    version.storageObject(),
                    result.rowCount(),
                    VersionStatus.READY,
                    version.createdBy(),
                    version.createdAt(),
                    Instant.now()
            );
            datasetRepository.saveVersion(readyVersion);

        } catch (Exception e) {
            // 4. Mark FAILED if an error occurs
            updateStatus(version, VersionStatus.FAILED);
            throw new RuntimeException("Failed to process dataset version", e);
        }
    }

    private void updateStatus(DatasetVersion version, VersionStatus newStatus) {
        DatasetVersion updated = new DatasetVersion(
                version.id(),
                version.datasetId(),
                version.semanticVersion(),
                version.schemaId(),
                version.originalFilename(),
                version.storageObject(),
                version.rowCount(),
                newStatus,
                version.createdBy(),
                version.createdAt(),
                Instant.now()
        );
        datasetRepository.saveVersion(updated);
    }
}
