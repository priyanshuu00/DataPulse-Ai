package com.datapulse.ai.core.domain;

public record DataQualityScore(
    double completeness,
    double accuracy,
    double consistency,
    double validity,
    double overallScore
) {
    public DataQualityScore {
        if (completeness < 0 || completeness > 100) throw new IllegalArgumentException("Completeness must be between 0 and 100");
        if (accuracy < 0 || accuracy > 100) throw new IllegalArgumentException("Accuracy must be between 0 and 100");
        if (consistency < 0 || consistency > 100) throw new IllegalArgumentException("Consistency must be between 0 and 100");
        if (validity < 0 || validity > 100) throw new IllegalArgumentException("Validity must be between 0 and 100");
        if (overallScore < 0 || overallScore > 100) throw new IllegalArgumentException("Overall score must be between 0 and 100");
    }
}
