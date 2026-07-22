CREATE TABLE datasets (
    id UUID PRIMARY KEY,
    workspace_id UUID NOT NULL,
    data_source_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_datasets_workspace_id ON datasets(workspace_id);
CREATE INDEX idx_datasets_name ON datasets(name);
CREATE INDEX idx_datasets_deleted_at ON datasets(deleted_at) WHERE deleted_at IS NULL;

-- Future tables will be added here or in V2. Currently we only need datasets for Phase 4.
