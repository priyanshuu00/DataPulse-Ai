package com.datapulse.ai.infrastructure.adapter;

import com.datapulse.ai.core.domain.ColumnMetadata;
import com.datapulse.ai.core.domain.DatasetSchema;
import com.datapulse.ai.core.domain.ProcessingResult;
import com.datapulse.ai.core.domain.StorageObject;
import com.datapulse.ai.core.ports.DataProcessorPort;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@ConditionalOnBean(SparkSession.class)
public class SparkDataProcessorAdapter implements DataProcessorPort {

    private static final Logger log = LoggerFactory.getLogger(SparkDataProcessorAdapter.class);
    private final SparkSession sparkSession;

    public SparkDataProcessorAdapter(SparkSession sparkSession) {
        this.sparkSession = sparkSession;
    }

    @Override
    public ProcessingResult processDataset(UUID datasetId, UUID versionId, StorageObject storageObject) {
        long startTime = System.currentTimeMillis();
        log.info("Starting Spark processing for datasetId={} versionId={}", datasetId, versionId);

        // Construct the S3A path for Spark
        String s3aPath = String.format("s3a://%s/%s", storageObject.bucket(), storageObject.objectKey());
        
        Dataset<Row> df;
        String format = "csv"; // simplified for Phase 7, could infer from extension
        
        try {
            if (storageObject.objectKey().endsWith(".parquet")) {
                format = "parquet";
                df = sparkSession.read().parquet(s3aPath);
            } else {
                df = sparkSession.read()
                        .option("header", "true")
                        .option("inferSchema", "true")
                        .csv(s3aPath);
            }

            long rowCount = df.count();
            StructType schema = df.schema();
            
            List<ColumnMetadata> columns = new ArrayList<>();
            for (StructField field : schema.fields()) {
                columns.add(new ColumnMetadata(
                        UUID.randomUUID(),
                        field.name(),
                        field.dataType().typeName(),
                        field.nullable(),
                        "Inferred from Spark",
                        0L, // Null count computation is heavy, skipped for brevity
                        ""  // Sample skipped for brevity
                ));
            }

            DatasetSchema datasetSchema = new DatasetSchema(
                    UUID.randomUUID(),
                    datasetId,
                    versionId,
                    columns
            );

            long duration = System.currentTimeMillis() - startTime;
            log.info("Completed Spark processing for versionId={} in {}ms. Rows: {}, Cols: {}", 
                    versionId, duration, rowCount, columns.size());

            return new ProcessingResult(
                    versionId,
                    datasetSchema,
                    rowCount,
                    columns.size(),
                    storageObject.size(),
                    format,
                    duration
            );

        } catch (Exception e) {
            log.error("Spark processing failed for versionId={}", versionId, e);
            throw new RuntimeException("Failed to process dataset via Spark", e);
        }
    }
}
