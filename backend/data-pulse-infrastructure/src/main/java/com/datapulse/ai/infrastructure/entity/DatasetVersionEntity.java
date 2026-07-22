package com.datapulse.ai.infrastructure.entity;

import com.datapulse.ai.core.domain.VersionStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "dataset_versions")
@SQLRestriction("deleted_at IS NULL")
public class DatasetVersionEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "dataset_id", nullable = false)
    private UUID datasetId;

    @Column(name = "semantic_version", nullable = false)
    private String semanticVersion;

    @Column(name = "schema_id")
    private UUID schemaId;

    @Column(name = "original_filename")
    private String originalFilename;

    @Embedded
    private StorageObjectEntity storageObject;

    @Column(name = "row_count")
    private Long rowCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VersionStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public DatasetVersionEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getDatasetId() { return datasetId; }
    public void setDatasetId(UUID datasetId) { this.datasetId = datasetId; }
    public String getSemanticVersion() { return semanticVersion; }
    public void setSemanticVersion(String semanticVersion) { this.semanticVersion = semanticVersion; }
    public UUID getSchemaId() { return schemaId; }
    public void setSchemaId(UUID schemaId) { this.schemaId = schemaId; }
    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
    public StorageObjectEntity getStorageObject() { return storageObject; }
    public void setStorageObject(StorageObjectEntity storageObject) { this.storageObject = storageObject; }
    public Long getRowCount() { return rowCount; }
    public void setRowCount(Long rowCount) { this.rowCount = rowCount; }
    public VersionStatus getStatus() { return status; }
    public void setStatus(VersionStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public Instant getDeletedAt() { return deletedAt; }
    public void setDeletedAt(Instant deletedAt) { this.deletedAt = deletedAt; }
}
