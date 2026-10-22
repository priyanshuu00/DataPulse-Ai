package com.datapulse.ai.infrastructure.repository;

import com.datapulse.ai.core.domain.ColumnMetadata;
import com.datapulse.ai.core.domain.DatasetSchema;
import com.datapulse.ai.core.ports.DatasetSchemaRepositoryPort;
import com.datapulse.ai.infrastructure.entity.ColumnMetadataEntity;
import com.datapulse.ai.infrastructure.entity.DatasetSchemaEntity;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class PostgresDatasetSchemaRepository implements DatasetSchemaRepositoryPort {

    private final SpringDataDatasetSchemaRepository springDataRepository;

    public PostgresDatasetSchemaRepository(SpringDataDatasetSchemaRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public DatasetSchema save(DatasetSchema schema) {
        DatasetSchemaEntity entity = new DatasetSchemaEntity();
        entity.setId(schema.id());
        entity.setDatasetId(schema.datasetId());
        entity.setVersionId(schema.versionId());

        if (schema.columns() != null) {
            entity.setColumns(schema.columns().stream().map(col -> {
                ColumnMetadataEntity colEntity = new ColumnMetadataEntity();
                colEntity.setId(col.id());
                colEntity.setName(col.name());
                colEntity.setDataType(col.dataType());
                colEntity.setNullable(col.isNullable());
                colEntity.setDescription(col.description());
                colEntity.setNullCount(col.nullCount());
                colEntity.setSampleValues(col.sampleValues());
                // Back-reference so the child carries the schema_id FK on insert.
                colEntity.setSchema(entity);
                return colEntity;
            }).collect(Collectors.toList()));
        }

        DatasetSchemaEntity saved = springDataRepository.save(entity);
        return mapToDomain(saved);
    }

    @Override
    public Optional<DatasetSchema> findByVersionId(UUID versionId) {
        return springDataRepository.findByVersionId(versionId).map(this::mapToDomain);
    }

    private DatasetSchema mapToDomain(DatasetSchemaEntity entity) {
        return new DatasetSchema(
                entity.getId(),
                entity.getDatasetId(),
                entity.getVersionId(),
                entity.getColumns() != null ? entity.getColumns().stream().map(col -> new ColumnMetadata(
                        col.getId(),
                        col.getName(),
                        col.getDataType(),
                        col.isNullable(),
                        col.getDescription(),
                        col.getNullCount(),
                        col.getSampleValues()
                )).collect(Collectors.toList()) : null
        );
    }
}
