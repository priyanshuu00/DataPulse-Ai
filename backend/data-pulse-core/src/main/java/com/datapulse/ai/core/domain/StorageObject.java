package com.datapulse.ai.core.domain;

public record StorageObject(
    String bucket,
    String objectKey,
    String uri,
    String etag,
    long size,
    String contentType,
    String checksum,
    StorageProvider provider
) {}
