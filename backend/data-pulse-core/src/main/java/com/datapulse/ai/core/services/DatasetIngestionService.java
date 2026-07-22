package com.datapulse.ai.core.services;

import com.datapulse.ai.core.domain.Dataset;
import com.datapulse.ai.core.domain.DatasetVersion;
import com.datapulse.ai.core.domain.StorageObject;
import com.datapulse.ai.core.domain.VersionStatus;
import com.datapulse.ai.core.ports.DatasetRepository;
import com.datapulse.ai.core.ports.ObjectStoragePort;

import com.datapulse.ai.core.ports.DatasetEventPublisher;

import java.io.InputStream;
import java.time.Instant;
import java.time.YearMonth;
import java.util.UUID;

public class DatasetIngestionService {

    private final ObjectStoragePort storagePort;
    private final DatasetRepository datasetRepository;
    private final VersionManagementService versionManagementService;
    private final DatasetEventPublisher eventPublisher;

    public DatasetIngestionService(ObjectStoragePort storagePort, 
                                   DatasetRepository datasetRepository, 
                                   VersionManagementService versionManagementService,
                                   DatasetEventPublisher eventPublisher) {
        this.storagePort = storagePort;
        this.datasetRepository = datasetRepository;
        this.versionManagementService = versionManagementService;
        this.eventPublisher = eventPublisher;
    }

    public DatasetVersion ingestFile(UUID datasetId, String originalFilename, InputStream stream, String contentType, long size, String correlationId, String traceId) {
        Dataset dataset = datasetRepository.findById(datasetId)
                .orElseThrow(() -> new IllegalArgumentException("Dataset not found: " + datasetId));

        // Generate deterministic key
        UUID versionId = UUID.randomUUID();
        String yearMonth = YearMonth.now().toString().replace("-", "/"); // e.g. 2026/07
        String extension = getExtension(originalFilename);
        
        String objectKey = String.format("datasets/%s/%s/%s/%s/data%s", 
                dataset.workspaceId(), datasetId, versionId, yearMonth, extension);

        // Upload to storage (Checksum is generated internally by the port)
        StorageObject storageObject = storagePort.uploadFile("datapulse-raw", objectKey, stream, contentType, size);

        // Determine Semantic Version
        String semanticVersion = "1.0.0"; // Placeholder for actual version calculation

        DatasetVersion version = new DatasetVersion(
                versionId,
                datasetId,
                semanticVersion,
                null,
                originalFilename,
                storageObject,
                null,
                VersionStatus.UPLOADED,
                null,
                Instant.now(),
                Instant.now()
        );

        DatasetVersion savedVersion = datasetRepository.saveVersion(version);
        
        // Publish Event
        eventPublisher.publishVersionCreatedEvent(savedVersion, correlationId, traceId);
        
        return savedVersion;
    }
    
    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf("."));
    }
}
