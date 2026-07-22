package com.datapulse.ai.infrastructure.entity;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "dataset_schemas")
public class DatasetSchemaEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "dataset_id", nullable = false)
    private UUID datasetId;

    @Column(name = "version_id", nullable = false)
    private UUID versionId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "schema_id")
    private List<ColumnMetadataEntity> columns;

    public DatasetSchemaEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getDatasetId() { return datasetId; }
    public void setDatasetId(UUID datasetId) { this.datasetId = datasetId; }
    public UUID getVersionId() { return versionId; }
    public void setVersionId(UUID versionId) { this.versionId = versionId; }
    public List<ColumnMetadataEntity> getColumns() { return columns; }
    public void setColumns(List<ColumnMetadataEntity> columns) { this.columns = columns; }
}
