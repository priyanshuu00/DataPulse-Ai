package com.datapulse.ai.shared.dto;

public record SystemInfoResponse(
    String apiBaseUrl,
    String storageEndpoint,
    String storageBucket,
    String kafkaBootstrap,
    String appVersion,
    String environment
) {}
