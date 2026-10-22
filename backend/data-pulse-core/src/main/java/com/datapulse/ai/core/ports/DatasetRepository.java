package com.datapulse.ai.core.ports;

import com.datapulse.ai.core.domain.Dataset;
import com.datapulse.ai.core.domain.DatasetVersion;
import com.datapulse.ai.core.domain.VersionStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DatasetRepository {
    Dataset save(Dataset dataset);
    Optional<Dataset> findById(UUID id);
    List<Dataset> findByWorkspaceId(UUID workspaceId);

    DatasetVersion saveVersion(DatasetVersion version);
    Optional<DatasetVersion> findVersionById(UUID versionId);
    List<DatasetVersion> findVersionsByDatasetId(UUID datasetId);

    long countVersionsByStatus(VersionStatus status);

    List<Dataset> findAll();
    void deleteById(UUID id);
}
