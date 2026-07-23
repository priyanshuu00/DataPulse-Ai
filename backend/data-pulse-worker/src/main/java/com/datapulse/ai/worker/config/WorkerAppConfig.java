package com.datapulse.ai.worker.config;

import com.datapulse.ai.core.ports.DataProcessorPort;
import com.datapulse.ai.core.ports.DatasetRepository;
import com.datapulse.ai.core.services.SparkOrchestrationService;
import com.datapulse.ai.infrastructure.adapter.SparkDataProcessorAdapter;
import com.datapulse.ai.infrastructure.mapper.DatasetMapper;
import com.datapulse.ai.infrastructure.repository.PostgresDatasetRepository;
import com.datapulse.ai.infrastructure.repository.SpringDataDatasetRepository;
import com.datapulse.ai.infrastructure.repository.SpringDataDatasetVersionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.spark.sql.SparkSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkerAppConfig {

    @Bean
    public DatasetRepository datasetRepository(SpringDataDatasetRepository springDataRepository, 
                                               SpringDataDatasetVersionRepository versionRepository,
                                               DatasetMapper mapper) {
        return new PostgresDatasetRepository(springDataRepository, versionRepository, mapper);
    }

    @Bean
    public com.datapulse.ai.core.ports.DatasetSchemaRepositoryPort datasetSchemaRepositoryPort(
            com.datapulse.ai.infrastructure.repository.SpringDataDatasetSchemaRepository springDataRepository) {
        return new com.datapulse.ai.infrastructure.repository.PostgresDatasetSchemaRepository(springDataRepository);
    }

    @Bean
    public SparkOrchestrationService sparkOrchestrationService(DataProcessorPort dataProcessorPort, 
                                                               DatasetRepository datasetRepository,
                                                               com.datapulse.ai.core.ports.DatasetSchemaRepositoryPort schemaRepository) {
        return new SparkOrchestrationService(dataProcessorPort, datasetRepository, schemaRepository);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        return mapper;
    }
}
