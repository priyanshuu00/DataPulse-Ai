package com.datapulse.ai.api.service;

import com.datapulse.ai.core.domain.Dataset;
import com.datapulse.ai.core.domain.DatasetSchema;
import com.datapulse.ai.core.domain.DatasetVersion;
import com.datapulse.ai.core.domain.RuleRecommendation;
import com.datapulse.ai.core.domain.VersionStatus;
import com.datapulse.ai.core.ports.DatasetRepository;
import com.datapulse.ai.core.ports.DatasetSchemaRepositoryPort;
import com.datapulse.ai.core.services.DataQualityRecommendationService;
import com.datapulse.ai.shared.dto.DashboardSummaryResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final DatasetRepository datasetRepository;
    private final DatasetSchemaRepositoryPort schemaRepositoryPort;
    private final DataQualityRecommendationService recommendationService;
    private final String storageBucket;
    private final String storageEndpoint;

    public DashboardService(
            DatasetRepository datasetRepository,
            DatasetSchemaRepositoryPort schemaRepositoryPort,
            DataQualityRecommendationService recommendationService,
            @Value("${storage.minio.bucket:datapulse-raw}") String storageBucket,
            @Value("${storage.minio.endpoint:http://localhost:9000}") String storageEndpoint) {
        this.datasetRepository = datasetRepository;
        this.schemaRepositoryPort = schemaRepositoryPort;
        this.recommendationService = recommendationService;
        this.storageBucket = storageBucket;
        this.storageEndpoint = storageEndpoint;
    }

    public DashboardSummaryResponse getDashboardSummary() {
        List<Dataset> allDatasets = datasetRepository.findAll();
        long totalDatasets = allDatasets.size();

        // Weekly Growth Calculation
        Instant now = Instant.now();
        Instant oneWeekAgo = now.minus(Duration.ofDays(7));
        Instant twoWeeksAgo = now.minus(Duration.ofDays(14));

        long recentDatasets = allDatasets.stream()
                .filter(d -> d.createdAt() != null && d.createdAt().isAfter(oneWeekAgo))
                .count();
        long priorDatasets = allDatasets.stream()
                .filter(d -> d.createdAt() != null && d.createdAt().isAfter(twoWeeksAgo) && d.createdAt().isBefore(oneWeekAgo))
                .count();

        String weeklyGrowthText;
        Double weeklyGrowthPercent = null;
        if (priorDatasets > 0) {
            double pct = ((double) (recentDatasets - priorDatasets) / priorDatasets) * 100.0;
            weeklyGrowthPercent = pct;
            weeklyGrowthText = String.format("%s%.0f%% this week", pct >= 0 ? "+" : "", pct);
        } else if (recentDatasets > 0) {
            weeklyGrowthText = String.format("+%d dataset%s this week", recentDatasets, recentDatasets == 1 ? "" : "s");
            weeklyGrowthPercent = (double) recentDatasets;
        } else {
            weeklyGrowthText = "No additions this week";
        }

        // Job Status Calculations
        long uploading = datasetRepository.countVersionsByStatus(VersionStatus.UPLOADING);
        long uploaded = datasetRepository.countVersionsByStatus(VersionStatus.UPLOADED);
        long validating = datasetRepository.countVersionsByStatus(VersionStatus.VALIDATING);
        long processing = datasetRepository.countVersionsByStatus(VersionStatus.PROCESSING);
        long processingJobs = uploading + uploaded + validating + processing;

        long failedJobs = datasetRepository.countVersionsByStatus(VersionStatus.FAILED);
        long readyJobs = datasetRepository.countVersionsByStatus(VersionStatus.READY);

        long completedJobs = readyJobs + failedJobs;
        double pipelineReliability = completedJobs > 0 ? ((double) readyJobs / completedJobs) * 100.0 : 100.0;
        String reliabilityText = completedJobs > 0 
                ? String.format("%.1f%% Pipeline Reliability", pipelineReliability) 
                : "100% Reliability (0 failures recorded)";

        // Storage Usage
        long storageBytes = datasetRepository.getTotalStorageBytes();
        String storageHuman = formatBytes(storageBytes);

        // Ingestion Traffic (Hourly buckets over last 24h)
        List<DatasetVersion> allVersions = datasetRepository.findAllVersions();
        List<DashboardSummaryResponse.TrafficPointDto> traffic = computeTrafficPoints(allVersions);

        // AI Insights Digest (Aggregated from real recommendations across ingested versions)
        List<DashboardSummaryResponse.DashboardInsightDto> insights = extractTopInsights(allVersions);

        // Cluster Telemetry
        String sparkStatus = processingJobs > 0 ? "RUNNING" : "IDLE";
        String kafkaStatus = "HEALTHY";
        String storageStatus = storageBytes > 0 ? "ONLINE" : "READY";

        DashboardSummaryResponse.ClusterHealthDto clusterHealth = new DashboardSummaryResponse.ClusterHealthDto(
                kafkaStatus,
                sparkStatus,
                storageStatus,
                storageBucket,
                storageEndpoint
        );

        return new DashboardSummaryResponse(
                totalDatasets,
                weeklyGrowthText,
                weeklyGrowthPercent,
                processingJobs,
                failedJobs,
                readyJobs,
                pipelineReliability,
                reliabilityText,
                storageBytes,
                storageHuman,
                storageBucket,
                traffic,
                insights,
                clusterHealth
        );
    }

    private List<DashboardSummaryResponse.TrafficPointDto> computeTrafficPoints(List<DatasetVersion> allVersions) {
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:00").withZone(ZoneId.systemDefault());
        Instant now = Instant.now();

        // 6 distinct 4-hour slots over the last 24 hours: 20h ago, 16h ago, 12h ago, 8h ago, 4h ago, current
        List<DashboardSummaryResponse.TrafficPointDto> points = new ArrayList<>();
        int[] hourOffsets = { 20, 16, 12, 8, 4, 0 };

        for (int offset : hourOffsets) {
            Instant slotStart = now.minus(Duration.ofHours(offset + 4));
            Instant slotEnd = now.minus(Duration.ofHours(offset));

            long count = allVersions.stream()
                    .filter(v -> v.createdAt() != null && !v.createdAt().isBefore(slotStart) && v.createdAt().isBefore(slotEnd))
                    .count();

            String timeLabel = timeFmt.format(slotEnd);
            points.add(new DashboardSummaryResponse.TrafficPointDto(timeLabel, count, 100.0));
        }

        return points;
    }

    private List<DashboardSummaryResponse.DashboardInsightDto> extractTopInsights(List<DatasetVersion> allVersions) {
        List<DashboardSummaryResponse.DashboardInsightDto> insights = new ArrayList<>();

        // Find versions that have a schema attached
        for (DatasetVersion v : allVersions) {
            if (v.schemaId() != null) {
                try {
                    List<RuleRecommendation> recs = recommendationService.generateRecommendations(v.id());
                    for (RuleRecommendation r : recs) {
                        insights.add(new DashboardSummaryResponse.DashboardInsightDto(
                                r.id().toString(),
                                r.rule().ruleType() + " on `" + r.rule().columnName() + "`",
                                r.explanation(),
                                r.confidenceScore(),
                                r.rule().ruleType(),
                                r.source().name()
                        ));
                        if (insights.size() >= 5) break;
                    }
                } catch (Exception ignored) {
                    // Ignore schema lookup errors during dashboard synthesis
                }
            }
            if (insights.size() >= 5) break;
        }

        return insights;
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "B";
        return String.format("%.1f %s", bytes / Math.pow(1024, exp), pre);
    }
}
