package com.datapulse.ai.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({
    com.datapulse.ai.infrastructure.config.KafkaProducerConfig.class // If needed
})
public class DataPulseWorkerApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataPulseWorkerApplication.class, args);
    }
}
