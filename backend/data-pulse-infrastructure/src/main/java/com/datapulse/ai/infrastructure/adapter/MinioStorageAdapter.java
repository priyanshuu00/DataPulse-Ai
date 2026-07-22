package com.datapulse.ai.infrastructure.adapter;

import com.datapulse.ai.core.domain.StorageObject;
import com.datapulse.ai.core.domain.StorageProvider;
import com.datapulse.ai.core.ports.ObjectStoragePort;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.InputStream;
import java.security.MessageDigest;

public class MinioStorageAdapter implements ObjectStoragePort {

    private final S3Client s3Client;
    private final String endpoint;

    public MinioStorageAdapter(S3Client s3Client, String endpoint) {
        this.s3Client = s3Client;
        this.endpoint = endpoint;
    }

    @Override
    public StorageObject uploadFile(String bucket, String objectKey, InputStream stream, String contentType, long size) {
        try {
            // In a real production system we'd calculate SHA-256 while streaming to avoid loading to memory
            // For phase 5, we assume the InputStream is already processed or we wrap it in a DigestInputStream.
            // Placeholder checksum for compilation
            String checksum = "placeholder_sha256"; 

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectKey)
                    .contentType(contentType)
                    .build();

            PutObjectResponse response = s3Client.putObject(request, RequestBody.fromInputStream(stream, size));

            String uri = endpoint + "/" + bucket + "/" + objectKey;

            return new StorageObject(
                    bucket,
                    objectKey,
                    uri,
                    response.eTag(),
                    size,
                    contentType,
                    checksum,
                    StorageProvider.MINIO
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to MinIO", e);
        }
    }
}
