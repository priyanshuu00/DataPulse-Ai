package com.datapulse.ai.shared.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record DatasetCreateRequest(
    @NotNull(message = "Workspace ID is required")
    UUID workspaceId,
    
    @NotNull(message = "DataSource ID is required")
    UUID dataSourceId,
    
    @NotBlank(message = "Name is required")
    String name,
    
    String description
) {}
