package com.datapulse.ai.infrastructure.repository;

import com.datapulse.ai.infrastructure.entity.DatasetVersionEntity;
import com.datapulse.ai.core.domain.VersionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataDatasetVersionRepository
        extends JpaRepository<DatasetVersionEntity, UUID> {

    List<DatasetVersionEntity> findByDatasetId(UUID datasetId);

    List<DatasetVersionEntity> findByStatus(VersionStatus status);

    long countByStatus(VersionStatus status);
}