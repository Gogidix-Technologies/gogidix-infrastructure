package com.exalt.warehousing.management.dto.etl;

// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Data Transfer Object for data lake payload
 */
// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
public class DataLakePayloadDTO {
    
    /**
     * Name of the dataset in the data lake
     */
    private String datasetName;
    
    /**
     * Name of the table within the dataset
     */
    private String tableName;
    
    /**
     * The actual data to be stored
     */
    private Map<String, Object> data;
    
    /**
     * Unique identifier for the batch
     */
    private String batchId;
    
    /**
     * Source system identifier
     */
    private String source = "warehouse-management";

    // Constructors
    public DataLakePayloadDTO() {
        // Default constructor
    }

    public DataLakePayloadDTO(String datasetName, String tableName, Map<String, Object> data, String batchId, String source) {
        this.datasetName = datasetName;
        this.tableName = tableName;
        this.data = data;
        this.batchId = batchId;
        this.source = source;
    }

    // Getters
    public String getDatasetName() {
        return datasetName;
    }

    public String getTableName() {
        return tableName;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public String getBatchId() {
        return batchId;
    }

    public String getSource() {
        return source;
    }

    // Setters
    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public void setSource(String source) {
        this.source = source;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String datasetName;
        private String tableName;
        private Map<String, Object> data;
        private String batchId;
        private String source = "warehouse-management"; // Default value

        public Builder datasetName(String datasetName) {
            this.datasetName = datasetName;
            return this;
        }

        public Builder tableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public Builder data(Map<String, Object> data) {
            this.data = data;
            return this;
        }

        public Builder batchId(String batchId) {
            this.batchId = batchId;
            return this;
        }

        public Builder source(String source) {
            this.source = source;
            return this;
        }

        public DataLakePayloadDTO build() {
            return new DataLakePayloadDTO(datasetName, tableName, data, batchId, source);
        }
    }
}
