package com.datapulse.ai.infrastructure.repository;

import com.datapulse.ai.core.domain.Dataset;
import com.datapulse.ai.core.domain.DatasetVersion;
import com.datapulse.ai.core.ports.DatasetRepository;
import com.datapulse.ai.infrastructure.entity.DatasetEntity;
import com.datapulse.ai.infrastructure.mapper.DatasetMapper;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class PostgresDatasetRepository implements DatasetRepository {

    private final SpringDataDatasetRepository springDataRepository;
    private final SpringDataDatasetVersionRepository versionRepository;
    private final DatasetMapper mapper;

    public PostgresDatasetRepository(SpringDataDatasetRepository springDataRepository, 
                                     SpringDataDatasetVersionRepository versionRepository,
                                     DatasetMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.versionRepository = versionRepository;
        this.mapper = mapper;
    }

    @Override
    public Dataset save(Dataset dataset) {
        DatasetEntity entity = mapper.toEntity(dataset);
        // Ensure ID is generated if null
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(Instant.now());
        }
        entity.setUpdatedAt(Instant.now());
        
        DatasetEntity savedEntity = springDataRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Dataset> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Dataset> findByWorkspaceId(UUID workspaceId) {
        return springDataRepository.findByWorkspaceId(workspaceId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Dataset> findAll() {
        return springDataRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        // Soft delete logic
        springDataRepository.findById(id).ifPresent(entity -> {
            entity.setDeletedAt(Instant.now());
            springDataRepository.save(entity);
        });
    }

    @Override
    public DatasetVersion saveVersion(DatasetVersion version) {
        com.datapulse.ai.infrastructure.entity.DatasetVersionEntity entity = mapper.toEntity(version);
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(Instant.now());
        }
        entity.setUpdatedAt(Instant.now());
        
        com.datapulse.ai.infrastructure.entity.DatasetVersionEntity savedEntity = versionRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<DatasetVersion> findVersionById(UUID versionId) {
        return versionRepository.findById(versionId).map(mapper::toDomain);
    }

    @Override
    public List<DatasetVersion> findVersionsByDatasetId(UUID datasetId) {
        return versionRepository.findByDatasetId(datasetId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
