package com.datapulse.ai.core.ports;

import com.datapulse.ai.core.domain.DatasetSchema;
import com.datapulse.ai.core.domain.RuleRecommendation;

import java.util.List;

public interface AiServicePort {
    List<RuleRecommendation> enhanceRecommendations(DatasetSchema schema, List<RuleRecommendation> existingRules);
}
