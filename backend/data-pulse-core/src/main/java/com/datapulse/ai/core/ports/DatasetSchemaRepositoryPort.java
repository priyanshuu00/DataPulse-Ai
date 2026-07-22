package com.datapulse.ai.core.ports;

import com.datapulse.ai.core.domain.DatasetSchema;

import java.util.Optional;
import java.util.UUID;

public interface DatasetSchemaRepositoryPort {
    DatasetSchema save(DatasetSchema schema);
    Optional<DatasetSchema> findByVersionId(UUID versionId);
}
