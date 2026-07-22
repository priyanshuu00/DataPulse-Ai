CREATE TABLE dataset_versions (
    id UUID PRIMARY KEY,
    dataset_id UUID NOT NULL REFERENCES datasets(id),
    semantic_version VARCHAR(50) NOT NULL,
    schema_id UUID,
    original_filename VARCHAR(255),
    
    -- Storage Object Embedded Fields
    bucket VARCHAR(255),
    object_key VARCHAR(1024),
    uri VARCHAR(2048),
    etag VARCHAR(255),
    size BIGINT,
    content_type VARCHAR(255),
    checksum VARCHAR(255),
    provider VARCHAR(50),
    
    row_count BIGINT,
    status VARCHAR(50) NOT NULL,
    
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_dataset_versions_dataset_id ON dataset_versions(dataset_id);
CREATE INDEX idx_dataset_versions_status ON dataset_versions(status);
CREATE INDEX idx_dataset_versions_deleted_at ON dataset_versions(deleted_at) WHERE deleted_at IS NULL;
