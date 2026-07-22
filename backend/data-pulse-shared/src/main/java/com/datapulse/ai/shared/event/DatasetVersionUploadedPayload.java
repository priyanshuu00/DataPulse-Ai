package com.datapulse.ai.shared.event;

import java.util.UUID;

public record DatasetVersionUploadedPayload(
    UUID datasetId,
    UUID versionId,
    UUID workspaceId,
    String objectKey,
    String checksum
) {}
