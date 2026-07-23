package com.datapulse.ai.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.datapulse.ai")
@EnableJpaRepositories(basePackages = "com.datapulse.ai.infrastructure.repository")
@EntityScan(basePackages = "com.datapulse.ai.infrastructure.entity")
@Import({
    com.datapulse.ai.infrastructure.config.KafkaProducerConfig.class // If needed
})
public class DataPulseWorkerApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataPulseWorkerApplication.class, args);
    }
}
