package com.datapulse.ai.infrastructure.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "column_metadata")
public class ColumnMetadataEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "schema_id", nullable = false, insertable = false, updatable = false)
    private UUID schemaId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "data_type", nullable = false)
    private String dataType;

    @Column(name = "is_nullable")
    private boolean isNullable;

    @Column(name = "description")
    private String description;

    @Column(name = "null_count")
    private Long nullCount;

    @Column(name = "sample_values")
    private String sampleValues;

    public ColumnMetadataEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getSchemaId() { return schemaId; }
    public void setSchemaId(UUID schemaId) { this.schemaId = schemaId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }
    public boolean isNullable() { return isNullable; }
    public void setNullable(boolean nullable) { isNullable = nullable; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getNullCount() { return nullCount; }
    public void setNullCount(Long nullCount) { this.nullCount = nullCount; }
    public String getSampleValues() { return sampleValues; }
    public void setSampleValues(String sampleValues) { this.sampleValues = sampleValues; }
}
