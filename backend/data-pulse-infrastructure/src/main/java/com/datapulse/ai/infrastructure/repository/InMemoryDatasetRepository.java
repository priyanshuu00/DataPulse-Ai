package com.datapulse.ai.infrastructure.repository;

import com.datapulse.ai.core.domain.Dataset;
import com.datapulse.ai.core.domain.DatasetVersion;
import com.datapulse.ai.core.domain.VersionStatus;
import com.datapulse.ai.core.ports.DatasetRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryDatasetRepository implements DatasetRepository {

    private final Map<UUID, Dataset> datasets = new ConcurrentHashMap<>();
    private final Map<UUID, DatasetVersion> versions = new ConcurrentHashMap<>();

    @Override
    public Dataset save(Dataset dataset) {
        UUID id = dataset.id() != null ? dataset.id() : UUID.randomUUID();
        Dataset toSave = new Dataset(
            id,
            dataset.workspaceId(),
            dataset.dataSourceId(),
            dataset.name(),
            dataset.description(),
            dataset.createdAt() != null ? dataset.createdAt() : java.time.Instant.now(),
            java.time.Instant.now()
        );
        datasets.put(id, toSave);
        return toSave;
    }

    @Override
    public Optional<Dataset> findById(UUID id) {
        return Optional.ofNullable(datasets.get(id));
    }

    @Override
    public List<Dataset> findByWorkspaceId(UUID workspaceId) {
        return datasets.values().stream()
            .filter(d -> workspaceId.equals(d.workspaceId()))
            .collect(Collectors.toList());
    }

    @Override
    public DatasetVersion saveVersion(DatasetVersion version) {
        UUID id = version.id() != null ? version.id() : UUID.randomUUID();
        DatasetVersion toSave = new DatasetVersion(
            id,
            version.datasetId(),
            version.semanticVersion(),
            version.schemaId(),
            version.originalFilename(),
            version.storageObject(),
            version.rowCount(),
            version.status(),
            version.createdBy(),
            version.createdAt() != null ? version.createdAt() : java.time.Instant.now(),
            version.updatedAt() != null ? version.updatedAt() : java.time.Instant.now()
        );
        versions.put(id, toSave);
        return toSave;
    }

    @Override
    public Optional<DatasetVersion> findVersionById(UUID versionId) {
        return Optional.ofNullable(versions.get(versionId));
    }

    @Override
    public List<DatasetVersion> findVersionsByDatasetId(UUID datasetId) {
        return versions.values().stream()
            .filter(v -> datasetId.equals(v.datasetId()))
            .collect(Collectors.toList());
    }
    
    @Override
    public long countVersionsByStatus(VersionStatus status) {
        return versions.values().stream()
            .filter(v -> status.equals(v.status()))
            .count();
    }

    @Override
    public void deleteById(UUID id) {
        datasets.remove(id);
    }
    
    @Override
    public List<Dataset> findAll() {
        return new ArrayList<>(datasets.values());
    }
}
