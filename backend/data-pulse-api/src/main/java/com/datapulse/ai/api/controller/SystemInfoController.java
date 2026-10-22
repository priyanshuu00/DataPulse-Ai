package com.datapulse.ai.api.controller;

import com.datapulse.ai.shared.dto.SystemInfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/system")
public class SystemInfoController {

    private final String serverPort;
    private final String minioEndpoint;
    private final String minioBucket;
    private final String kafkaBootstrap;

    public SystemInfoController(
            @Value("${server.port:8081}") String serverPort,
            @Value("${storage.minio.endpoint:http://localhost:9000}") String minioEndpoint,
            @Value("${storage.minio.bucket:datapulse-raw}") String minioBucket,
            @Value("${spring.kafka.bootstrap-servers:localhost:9092}") String kafkaBootstrap) {
        this.serverPort = serverPort;
        this.minioEndpoint = minioEndpoint;
        this.minioBucket = minioBucket;
        this.kafkaBootstrap = kafkaBootstrap;
    }

    @GetMapping("/info")
    public ResponseEntity<SystemInfoResponse> getSystemInfo() {
        return ResponseEntity.ok(new SystemInfoResponse(
                "http://localhost:" + serverPort + "/api/v1",
                minioEndpoint,
                minioBucket,
                kafkaBootstrap,
                "v1.0.0-RELEASE",
                "Development / Sandbox"
        ));
    }
}
