package com.datapulse.ai.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.datapulse.ai")
@EnableJpaRepositories(basePackages = "com.datapulse.ai.infrastructure.repository")
@EntityScan(basePackages = "com.datapulse.ai.infrastructure.entity")
public class DataPulseApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataPulseApplication.class, args);
    }
}
