package com.datapulse.ai.core.ports;

import com.datapulse.ai.core.domain.PipelineRun;
import java.util.Optional;
import java.util.UUID;

public interface PipelineRunRepository {
    PipelineRun save(PipelineRun run);
    Optional<PipelineRun> findById(UUID id);
}
