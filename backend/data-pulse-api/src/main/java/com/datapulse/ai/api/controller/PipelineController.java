package com.datapulse.ai.api.controller;

import com.datapulse.ai.core.domain.Dataset;
import com.datapulse.ai.core.domain.DatasetVersion;
import com.datapulse.ai.core.domain.VersionStatus;
import com.datapulse.ai.core.ports.DatasetRepository;
import com.datapulse.ai.shared.dto.DashboardSummaryResponse.ClusterHealthDto;
import com.datapulse.ai.shared.dto.PipelineJobResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/pipeline")
public class PipelineController {

    private final DatasetRepository datasetRepository;
    private final String datasetEventsTopic;
    private final String minioEndpoint;
    private final String minioBucket;

    public PipelineController(
            DatasetRepository datasetRepository,
            @Value("${datapulse.kafka.topics.dataset-events:datapulse.datasets.events}") String datasetEventsTopic,
            @Value("${storage.minio.endpoint:http://localhost:9000}") String minioEndpoint,
            @Value("${storage.minio.bucket:datapulse-raw}") String minioBucket) {
        this.datasetRepository = datasetRepository;
        this.datasetEventsTopic = datasetEventsTopic;
        this.minioEndpoint = minioEndpoint;
        this.minioBucket = minioBucket;
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<PipelineJobResponse>> getPipelineJobs() {
        Map<UUID, String> datasetNames = datasetRepository.findAll().stream()
                .collect(Collectors.toMap(Dataset::id, Dataset::name, (a, b) -> a));

        List<PipelineJobResponse> jobs = datasetRepository.findAllVersions().stream()
                .sorted(Comparator.comparing(DatasetVersion::createdAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(v -> {
                    String dsName = datasetNames.getOrDefault(v.datasetId(), "Dataset-" + v.datasetId().toString().substring(0, 8));
                    String jobId = "JOB-" + v.id().toString().substring(0, 8).toUpperCase();
                    String stage = v.status() != null ? v.status().name() : "UPLOADED";

                    String kafkaStatus = v.status() == VersionStatus.FAILED ? "ERROR" :
                            (v.status() == VersionStatus.UPLOADING ? "QUEUED" : "HEALTHY");

                    String sparkStatus = v.status() == VersionStatus.PROCESSING ? "RUNNING" :
                            (v.status() == VersionStatus.READY ? "COMPLETED" :
                                    (v.status() == VersionStatus.FAILED ? "FAILED" : "PENDING"));

                    String duration = computeDuration(v.createdAt(), v.updatedAt());

                    return new PipelineJobResponse(
                            jobId,
                            v.datasetId(),
                            dsName,
                            v.semanticVersion(),
                            stage,
                            kafkaStatus,
                            sparkStatus,
                            duration,
                            datasetEventsTopic,
                            v.createdAt()
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/cluster")
    public ResponseEntity<ClusterHealthDto> getClusterHealth() {
        long activeJobs = datasetRepository.countVersionsByStatus(VersionStatus.PROCESSING)
                + datasetRepository.countVersionsByStatus(VersionStatus.UPLOADING)
                + datasetRepository.countVersionsByStatus(VersionStatus.VALIDATING);

        String sparkStatus = activeJobs > 0 ? "ACTIVE (" + activeJobs + " Running)" : "STANDBY";

        return ResponseEntity.ok(new ClusterHealthDto(
                "HEALTHY",
                sparkStatus,
                "ONLINE",
                minioBucket,
                minioEndpoint
        ));
    }

    private String computeDuration(Instant start, Instant end) {
        if (start == null) return "0s";
        Instant to = (end != null && end.isAfter(start)) ? end : Instant.now();
        long seconds = Duration.between(start, to).getSeconds();
        if (seconds < 60) {
            return Math.max(1, seconds) + "s";
        }
        long mins = seconds / 60;
        long remSec = seconds % 60;
        return mins + "m " + remSec + "s";
    }
}
