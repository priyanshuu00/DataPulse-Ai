package com.datapulse.ai.infrastructure.repository;

import com.datapulse.ai.infrastructure.entity.DatasetSchemaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataDatasetSchemaRepository extends JpaRepository<DatasetSchemaEntity, UUID> {
    Optional<DatasetSchemaEntity> findByVersionId(UUID versionId);
}
