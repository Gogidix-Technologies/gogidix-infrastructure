package com.exalt.warehousing.analytics.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Analytics Report Entity
 * 
 * Represents generated analytics reports with metadata and execution details
 */
@Entity
@Table(name = "analytics_reports")
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsReport {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    @Column(name = "report_name", nullable = false)
    private String reportName;

    @NotNull
    @Column(name = "report_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    @NotNull
    @Column(name = "report_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private MetricCategory reportCategory;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Column(name = "period_start", nullable = false)
    private LocalDateTime periodStart;

    @NotNull
    @Column(name = "period_end", nullable = false)
    private LocalDateTime periodEnd;

    @Column(name = "warehouse_id")
    private UUID warehouseId;

    @Column(name = "region")
    private String region;

    @Column(name = "country")
    private String country;

    @NotNull
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    @Column(name = "format")
    @Enumerated(EnumType.STRING)
    private ReportFormat format;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "execution_time_ms")
    private Long executionTimeMs;

    @Column(name = "total_records")
    private Long totalRecords;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    // Report parameters and filters
    @ElementCollection
    @CollectionTable(name = "report_parameters", joinColumns = @JoinColumn(name = "report_id"))
    @MapKeyColumn(name = "parameter_key")
    @Column(name = "parameter_value")
    @Builder.Default
    private Map<String, String> parameters = new HashMap<>();

    // Report configuration
    @ElementCollection
    @CollectionTable(name = "report_config", joinColumns = @JoinColumn(name = "report_id"))
    @MapKeyColumn(name = "config_key")
    @Column(name = "config_value")
    @Builder.Default
    private Map<String, String> configuration = new HashMap<>();

    // Report summary statistics
    @ElementCollection
    @CollectionTable(name = "report_summary", joinColumns = @JoinColumn(name = "report_id"))
    @MapKeyColumn(name = "summary_key")
    @Column(name = "summary_value")
    @Builder.Default
    private Map<String, String> summary = new HashMap<>();

    @NotNull
    @Column(name = "generated_by", nullable = false)
    private String generatedBy;

    @Column(name = "shared_with")
    private String sharedWith;

    @Column(name = "access_level")
    @Enumerated(EnumType.STRING)
    private AccessLevel accessLevel;

    @Column(name = "download_count")
    @Builder.Default
    private Integer downloadCount = 0;
    
    @Lob
    @Column(name = "content_data")
    private byte[] contentData;
    
    @Column(name = "report_status")
    @Enumerated(EnumType.STRING)
    private ReportStatus reportStatus;

    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;

    @Column(name = "last_accessed_by")
    private String lastAccessedBy;

    // Audit Fields
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Check if report is completed successfully
     */
    public boolean isCompleted() {
        return status == ReportStatus.COMPLETED;
    }

    /**
     * Check if report failed
     */
    public boolean isFailed() {
        return status == ReportStatus.FAILED;
    }

    /**
     * Check if report is expired
     */
    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }

    /**
     * Get execution duration in seconds
     */
    public Double getExecutionDurationSeconds() {
        if (startedAt != null && completedAt != null) {
            return (double) java.time.Duration.between(startedAt, completedAt).toMillis() / 1000.0;
        }
        return null;
    }

    /**
     * Add parameter to report
     */
    public void addParameter(String key, String value) {
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        parameters.put(key, value);
    }

    /**
     * Add configuration to report
     */
    public void addConfiguration(String key, String value) {
        if (configuration == null) {
            configuration = new HashMap<>();
        }
        configuration.put(key, value);
    }

    /**
     * Add summary statistic
     */
    public void addSummary(String key, String value) {
        if (summary == null) {
            summary = new HashMap<>();
        }
        summary.put(key, value);
    }

    /**
     * Increment download count
     */
    public void incrementDownloadCount() {
        if (downloadCount == null) {
            downloadCount = 0;
        }
        downloadCount++;
        lastAccessedAt = LocalDateTime.now();
    }

    /**
     * Update access information
     */
    public void updateAccess(String accessedBy) {
        lastAccessedAt = LocalDateTime.now();
        lastAccessedBy = accessedBy;
    }

    /**
     * Get file size in MB
     */
    public Double getFileSizeMB() {
        if (fileSize != null) {
            return fileSize / (1024.0 * 1024.0);
        }
        return null;
    }
}