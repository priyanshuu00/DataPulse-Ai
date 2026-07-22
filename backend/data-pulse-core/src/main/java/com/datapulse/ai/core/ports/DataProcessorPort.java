package com.datapulse.ai.core.ports;

import com.datapulse.ai.core.domain.ProcessingResult;
import com.datapulse.ai.core.domain.StorageObject;

import java.util.UUID;

public interface DataProcessorPort {
    ProcessingResult processDataset(UUID datasetId, UUID versionId, StorageObject storageObject);
}
