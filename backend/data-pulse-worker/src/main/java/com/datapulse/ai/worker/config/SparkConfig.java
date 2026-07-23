package com.datapulse.ai.worker.config;

import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkConfig {

    @Value("${spark.master}")
    private String sparkMaster;

    @Value("${spark.app.name}")
    private String sparkAppName;

    @Value("${storage.minio.endpoint}")
    private String minioEndpoint;

    @Value("${storage.minio.access-key}")
    private String minioAccessKey;

    @Value("${storage.minio.secret-key}")
    private String minioSecretKey;

    @Bean
    public SparkSession sparkSession() {
        return SparkSession.builder()
                .appName(sparkAppName)
                .master(sparkMaster)
                .config("spark.hadoop.fs.s3a.endpoint", minioEndpoint)
                .config("spark.hadoop.fs.s3a.access.key", minioAccessKey)
                .config("spark.hadoop.fs.s3a.secret.key", minioSecretKey)
                .config("spark.hadoop.fs.s3a.path.style.access", "true")
                .config("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
                // Optimization for MinIO & Spring Boot 3 compatibility
                .config("spark.ui.enabled", "false")
                .config("spark.hadoop.fs.s3a.connection.ssl.enabled", "false")
                .getOrCreate();
    }
}
