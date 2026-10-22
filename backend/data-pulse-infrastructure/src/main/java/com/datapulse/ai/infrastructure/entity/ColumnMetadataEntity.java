package com.datapulse.ai.infrastructure.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "column_metadata")
public class ColumnMetadataEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    // The association owns the schema_id foreign key. A plain read-only @Column here
    // (with @JoinColumn on the parent side) made Hibernate insert children before the
    // parent row existed, violating the NOT NULL constraint on schema_id.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schema_id", nullable = false)
    private DatasetSchemaEntity schema;

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
    public DatasetSchemaEntity getSchema() { return schema; }
    public void setSchema(DatasetSchemaEntity schema) { this.schema = schema; }
    public UUID getSchemaId() { return schema != null ? schema.getId() : null; }
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
