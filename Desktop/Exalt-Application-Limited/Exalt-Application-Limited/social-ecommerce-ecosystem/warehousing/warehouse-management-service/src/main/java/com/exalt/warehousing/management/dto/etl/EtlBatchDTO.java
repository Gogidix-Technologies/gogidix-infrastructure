package com.exalt.warehousing.management.dto.etl;

// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for ETL batch operations
 */
// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
public class EtlBatchDTO {
    
    /**
     * Name of the dataset in the data lake
     */
    private String datasetName;
    
    /**
     * Name of the table within the dataset
     */
    private String tableName;
    
    /**
     * List of records to be processed in this batch
     */
    private List<Map<String, Object>> records;
    
    /**
     * Batch operation description
     */
    private String description;
    
    /**
     * Batch creation timestamp
     */
    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructors
    public EtlBatchDTO() {
        // Default constructor
    }

    public EtlBatchDTO(String datasetName, String tableName, List<Map<String, Object>> records, String description, LocalDateTime createdAt) {
        this.datasetName = datasetName;
        this.tableName = tableName;
        this.records = records;
        this.description = description;
        this.createdAt = createdAt;
    }

    // Getters
    public String getDatasetName() {
        return datasetName;
    }

    public String getTableName() {
        return tableName;
    }

    public List<Map<String, Object>> getRecords() {
        return records;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setRecords(List<Map<String, Object>> records) {
        this.records = records;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String datasetName;
        private String tableName;
        private List<Map<String, Object>> records;
        private String description;
        private LocalDateTime createdAt = LocalDateTime.now();

        public Builder datasetName(String datasetName) {
            this.datasetName = datasetName;
            return this;
        }

        public Builder tableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public Builder records(List<Map<String, Object>> records) {
            this.records = records;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public EtlBatchDTO build() {
            return new EtlBatchDTO(datasetName, tableName, records, description, createdAt);
        }
    }
}
