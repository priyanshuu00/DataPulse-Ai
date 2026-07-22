CREATE TABLE dataset_schemas (
    id UUID PRIMARY KEY,
    dataset_id UUID NOT NULL REFERENCES datasets(id),
    version_id UUID NOT NULL REFERENCES dataset_versions(id)
);

CREATE TABLE column_metadata (
    id UUID PRIMARY KEY,
    schema_id UUID NOT NULL REFERENCES dataset_schemas(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    data_type VARCHAR(255) NOT NULL,
    is_nullable BOOLEAN DEFAULT true,
    description TEXT,
    null_count BIGINT,
    sample_values TEXT
);

CREATE INDEX idx_dataset_schemas_dataset_id ON dataset_schemas(dataset_id);
CREATE INDEX idx_dataset_schemas_version_id ON dataset_schemas(version_id);
CREATE INDEX idx_column_metadata_schema_id ON column_metadata(schema_id);
