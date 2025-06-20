package com.exalt.warehousing.management.dto.etl;

// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for ETL operation results
 */
// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
public class EtlResultDTO {
    
    /**
     * Flag indicating success or failure
     */
    private boolean success;
    
    /**
     * Message describing the operation result
     */
    private String message;
    
    /**
     * Batch ID of the processed batch
     */
    private String batchId;
    
    /**
     * Number of records processed
     */
    private Integer recordCount;
    
    /**
     * Timestamp of when the operation completed
     */
    private LocalDateTime timestamp = LocalDateTime.now();

    // Constructors
    public EtlResultDTO() {
        // Default constructor
    }

    public EtlResultDTO(boolean success, String message, String batchId, Integer recordCount, LocalDateTime timestamp) {
        this.success = success;
        this.message = message;
        this.batchId = batchId;
        this.recordCount = recordCount;
        this.timestamp = timestamp;
    }

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getBatchId() {
        return batchId;
    }

    public Integer getRecordCount() {
        return recordCount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public void setRecordCount(Integer recordCount) {
        this.recordCount = recordCount;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean success;
        private String message;
        private String batchId;
        private Integer recordCount;
        private LocalDateTime timestamp = LocalDateTime.now();

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder batchId(String batchId) {
            this.batchId = batchId;
            return this;
        }

        public Builder recordCount(Integer recordCount) {
            this.recordCount = recordCount;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public EtlResultDTO build() {
            return new EtlResultDTO(success, message, batchId, recordCount, timestamp);
        }
    }
}
