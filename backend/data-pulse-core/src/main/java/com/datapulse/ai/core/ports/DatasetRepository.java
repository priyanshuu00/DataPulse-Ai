package com.datapulse.ai.core.ports;

import com.datapulse.ai.core.domain.Dataset;
import com.datapulse.ai.core.domain.DatasetVersion;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface DatasetRepository {
    Dataset save(Dataset dataset);
    Optional<Dataset> findById(UUID id);
    List<Dataset> findByWorkspaceId(UUID workspaceId);
    
    DatasetVersion saveVersion(DatasetVersion version);
    Optional<DatasetVersion> findVersionById(UUID versionId);
    List<DatasetVersion> findVersionsByDatasetId(UUID datasetId);
    
    List<Dataset> findAll();
    void deleteById(UUID id);
}
