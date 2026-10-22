package com.datapulse.ai.shared.dto;

import java.util.List;

public record DashboardSummaryResponse(
    long totalDatasets,
    String weeklyGrowthText,
    Double weeklyGrowthPercent,
    long processingJobs,
    long failedJobs,
    long readyJobs,
    double pipelineReliability,
    String reliabilityText,
    long storageUsedBytes,
    String storageHumanReadable,
    String storageBucket,
    List<TrafficPointDto> ingestionTraffic,
    List<DashboardInsightDto> insights,
    ClusterHealthDto clusterHealth
) {
    public record TrafficPointDto(
        String time,
        long ingest,
        double quality
    ) {}

    public record DashboardInsightDto(
        String id,
        String title,
        String description,
        double confidenceScore,
        String type,
        String source
    ) {}

    public record ClusterHealthDto(
        String kafkaStatus,
        String sparkStatus,
        String storageStatus,
        String storageBucket,
        String storageEndpoint
    ) {}
}
