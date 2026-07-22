package com.datapulse.ai.infrastructure.repository;

import com.datapulse.ai.infrastructure.entity.DatasetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataDatasetRepository extends JpaRepository<DatasetEntity, UUID> {
    List<DatasetEntity> findByWorkspaceId(UUID workspaceId);
}
