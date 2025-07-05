package com.exalt.warehousing.fulfillment.entity;

import com.exalt.warehousing.fulfillment.enums.TaskStatus;
import com.exalt.warehousing.shared.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a packing task for a fulfillment order
 */
@Entity
@Table(name = "packing_tasks")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackingTask extends BaseEntity {

    @NotNull
    @Column(name = "fulfillment_order_id", nullable = false)
    private UUID fulfillmentOrderId;

    @Column(name = "assigned_staff_id")
    private UUID assignedStaffId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TaskStatus status;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "packing_station")
    private String packingStation;

    @Column(name = "instruction")
    private String instruction;

    @Column(name = "packaging_type")
    private String packagingType;

    @Column(name = "weight_kg")
    private Double weightKg;

    @Column(name = "length_cm")
    private Double lengthCm;

    @Column(name = "width_cm")
    private Double widthCm;

    @Column(name = "height_cm")
    private Double heightCm;

    @Column(name = "special_handling_required")
    private Boolean specialHandlingRequired;

    @Column(name = "special_handling_instructions")
    private String specialHandlingInstructions;

    @Column(name = "due_by")
    private LocalDateTime dueBy;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "completion_notes")
    private String completionNotes;

    /**
     * Check if task is overdue
     *
     * @return true if task is past due date and not completed
     */
    @Transient
    public boolean isOverdue() {
        return dueBy != null && 
               LocalDateTime.now().isAfter(dueBy) && 
               status != TaskStatus.COMPLETED && 
               status != TaskStatus.CANCELLED;
    }

    /**
     * Check if task is in progress
     *
     * @return true if task has been started but not completed
     */
    @Transient
    public boolean isInProgress() {
        return status == TaskStatus.IN_PROGRESS;
    }

    /**
     * Calculate the duration of task execution in minutes
     *
     * @return duration in minutes or null if task not completed
     */
    @Transient
    public Long getDurationMinutes() {
        if (startedAt != null && completedAt != null) {
            return java.time.Duration.between(startedAt, completedAt).toMinutes();
        }
        return null;
    }

    /**
     * Calculate volume in cubic centimeters
     *
     * @return volume or null if dimensions not set
     */
    @Transient
    public Double getVolumeCubicCm() {
        if (lengthCm != null && widthCm != null && heightCm != null) {
            return lengthCm * widthCm * heightCm;
        }
        return null;
    }

    /**
     * Start the task
     *
     * @param staffId ID of staff member starting the task
     */
    public void start(UUID staffId) {
        if (status == TaskStatus.PENDING) {
            this.status = TaskStatus.IN_PROGRESS;
            this.assignedStaffId = staffId;
            this.startedAt = LocalDateTime.now();
        }
    }

    /**
     * Complete the task
     *
     * @param notes completion notes
     * @param weightKg package weight
     * @param lengthCm package length
     * @param widthCm package width
     * @param heightCm package height
     */
    public void complete(String notes, Double weightKg, Double lengthCm, Double widthCm, Double heightCm) {
        if (status == TaskStatus.IN_PROGRESS) {
            this.status = TaskStatus.COMPLETED;
            this.completionNotes = notes;
            this.weightKg = weightKg;
            this.lengthCm = lengthCm;
            this.widthCm = widthCm;
            this.heightCm = heightCm;
            this.completedAt = LocalDateTime.now();
        }
    }

    /**
     * Cancel the task
     *
     * @param reason cancellation reason
     */
    public void cancel(String reason) {
        this.status = TaskStatus.CANCELLED;
        this.completionNotes = reason;
    }
}