package com.datapulse.ai.api.config;

import com.datapulse.ai.core.ports.DatasetRepository;
import com.datapulse.ai.core.ports.DatasetRepository;
import com.datapulse.ai.core.ports.ObjectStoragePort;
import com.datapulse.ai.core.services.DatasetIngestionService;
import com.datapulse.ai.core.services.VersionManagementService;
import com.datapulse.ai.infrastructure.adapter.MinioStorageAdapter;
import com.datapulse.ai.infrastructure.mapper.DatasetMapper;
import com.datapulse.ai.infrastructure.repository.PostgresDatasetRepository;
import com.datapulse.ai.infrastructure.repository.SpringDataDatasetRepository;
import com.datapulse.ai.infrastructure.repository.SpringDataDatasetVersionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AppConfig {

    @Bean
    public DatasetRepository datasetRepository(SpringDataDatasetRepository springDataRepository, 
                                               SpringDataDatasetVersionRepository versionRepository,
                                               DatasetMapper mapper) {
        return new PostgresDatasetRepository(springDataRepository, versionRepository, mapper);
    }

    @Bean
    public ObjectStoragePort objectStoragePort(S3Client s3Client, @Value("${storage.minio.endpoint}") String endpoint) {
        return new MinioStorageAdapter(s3Client, endpoint);
    }

    @Bean
    public com.datapulse.ai.core.ports.DatasetEventPublisher datasetEventPublisher(org.springframework.kafka.core.KafkaTemplate<String, Object> kafkaTemplate,
                                                       @Value("${datapulse.kafka.topics.dataset-events}") String topic) {
        return new com.datapulse.ai.infrastructure.event.KafkaDatasetEventPublisher(kafkaTemplate, topic);
    }

    @Bean
    public VersionManagementService versionManagementService() {
        return new VersionManagementService();
    }

    @Bean
    public DatasetIngestionService datasetIngestionService(ObjectStoragePort storagePort, 
                                                           DatasetRepository datasetRepository,
                                                           VersionManagementService versionManagementService,
                                                           com.datapulse.ai.core.ports.DatasetEventPublisher eventPublisher) {
        return new DatasetIngestionService(storagePort, datasetRepository, versionManagementService, eventPublisher);
    }

    @Bean
    public com.datapulse.ai.core.ports.DatasetSchemaRepositoryPort datasetSchemaRepositoryPort(
            com.datapulse.ai.infrastructure.repository.SpringDataDatasetSchemaRepository springDataRepository) {
        return new com.datapulse.ai.infrastructure.repository.PostgresDatasetSchemaRepository(springDataRepository);
    }

    @Bean
    public com.datapulse.ai.infrastructure.ai.PromptFactory promptFactory() {
        return new com.datapulse.ai.infrastructure.ai.PromptFactory();
    }

    @Bean
    public com.datapulse.ai.core.ports.AiServicePort aiServicePort(
            com.datapulse.ai.infrastructure.ai.PromptFactory promptFactory,
            com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
        return new com.datapulse.ai.infrastructure.ai.MockAiAdapter(promptFactory, objectMapper);
    }

    @Bean
    public com.datapulse.ai.core.services.DeterministicRuleEngine deterministicRuleEngine() {
        return new com.datapulse.ai.core.services.DeterministicRuleEngine();
    }

    @Bean
    public com.datapulse.ai.core.services.DataQualityRecommendationService dataQualityRecommendationService(
            com.datapulse.ai.core.ports.DatasetSchemaRepositoryPort schemaRepositoryPort,
            com.datapulse.ai.core.services.DeterministicRuleEngine deterministicRuleEngine,
            com.datapulse.ai.core.ports.AiServicePort aiServicePort) {
        return new com.datapulse.ai.core.services.DataQualityRecommendationService(schemaRepositoryPort, deterministicRuleEngine, aiServicePort);
    }
}
