package com.exalt.warehousing.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Summary information for a Zone entity
 * Used for displaying zone statistics and key metrics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZoneSummary {

    /**
     * Unique identifier for the zone
     */
    private UUID id;

    /**
     * Zone code/identifier
     */
    private String zoneCode;

    /**
     * Zone name
     */
    private String zoneName;

    /**
     * Zone type (STORAGE, PICKING, PACKING, RECEIVING, SHIPPING, etc.)
     */
    private String zoneType;

    /**
     * Current status of the zone
     */
    private String status;

    /**
     * Warehouse ID that this zone belongs to
     */
    private UUID warehouseId;

    /**
     * Warehouse name (for display purposes)
     */
    private String warehouseName;

    /**
     * Total number of locations in this zone
     */
    private Long totalLocations;

    /**
     * Number of available locations
     */
    private Long availableLocations;

    /**
     * Number of occupied locations
     */
    private Long occupiedLocations;

    /**
     * Number of blocked locations
     */
    private Long blockedLocations;

    /**
     * Total storage capacity in cubic meters
     */
    private Double totalCapacityCubicMeters;

    /**
     * Used storage capacity in cubic meters
     */
    private Double usedCapacityCubicMeters;

    /**
     * Available storage capacity in cubic meters
     */
    private Double availableCapacityCubicMeters;

    /**
     * Total weight capacity in kilograms
     */
    private Double totalWeightCapacityKg;

    /**
     * Used weight capacity in kilograms
     */
    private Double usedWeightCapacityKg;

    /**
     * Available weight capacity in kilograms
     */
    private Double availableWeightCapacityKg;

    /**
     * Number of active warehouse tasks in this zone
     */
    private Long activeTaskCount;

    /**
     * Number of pending warehouse tasks in this zone
     */
    private Long pendingTaskCount;

    /**
     * Number of completed warehouse tasks in this zone (today)
     */
    private Long completedTaskCountToday;

    /**
     * Average task completion time in minutes
     */
    private Double averageTaskCompletionMinutes;

    /**
     * Zone efficiency percentage
     */
    private Double efficiencyPercentage;

    /**
     * Zone utilization percentage by volume
     */
    private Double volumeUtilizationPercentage;

    /**
     * Zone utilization percentage by weight
     */
    private Double weightUtilizationPercentage;

    /**
     * Zone utilization percentage by location count
     */
    private Double locationUtilizationPercentage;

    /**
     * Temperature range for the zone (if applicable)
     */
    private String temperatureRange;

    /**
     * Indicates if this zone has special handling requirements
     */
    private Boolean hasSpecialHandling;

    /**
     * Indicates if this zone is climate controlled
     */
    private Boolean isClimateControlled;

    /**
     * Priority level of the zone for operations
     */
    private Integer priority;

    /**
     * Calculate location utilization percentage
     *
     * @return location utilization percentage
     */
    public Double calculateLocationUtilization() {
        if (totalLocations == null || totalLocations == 0) {
            return 0.0;
        }
        return (occupiedLocations.doubleValue() / totalLocations.doubleValue()) * 100.0;
    }

    /**
     * Calculate volume utilization percentage
     *
     * @return volume utilization percentage
     */
    public Double calculateVolumeUtilization() {
        if (totalCapacityCubicMeters == null || totalCapacityCubicMeters == 0 || usedCapacityCubicMeters == null) {
            return 0.0;
        }
        return (usedCapacityCubicMeters / totalCapacityCubicMeters) * 100.0;
    }

    /**
     * Calculate weight utilization percentage
     *
     * @return weight utilization percentage
     */
    public Double calculateWeightUtilization() {
        if (totalWeightCapacityKg == null || totalWeightCapacityKg == 0 || usedWeightCapacityKg == null) {
            return 0.0;
        }
        return (usedWeightCapacityKg / totalWeightCapacityKg) * 100.0;
    }

    /**
     * Calculate total active tasks
     *
     * @return total active tasks
     */
    public Long getTotalActiveTasks() {
        long active = activeTaskCount != null ? activeTaskCount : 0L;
        long pending = pendingTaskCount != null ? pendingTaskCount : 0L;
        return active + pending;
    }

    /**
     * Check if the zone is operating at capacity
     *
     * @return true if zone is near or at capacity
     */
    public Boolean isAtCapacity() {
        Double volumeUtil = calculateVolumeUtilization();
        Double weightUtil = calculateWeightUtilization();
        Double locationUtil = calculateLocationUtilization();
        
        return (volumeUtil != null && volumeUtil >= 90.0) ||
               (weightUtil != null && weightUtil >= 90.0) ||
               (locationUtil != null && locationUtil >= 90.0);
    }

    /**
     * Check if the zone needs attention (blocked locations, low efficiency, etc.)
     *
     * @return true if zone needs attention
     */
    public Boolean needsAttention() {
        return (blockedLocations != null && blockedLocations > 0) ||
               (efficiencyPercentage != null && efficiencyPercentage < 70.0) ||
               "MAINTENANCE".equals(status) ||
               "BLOCKED".equals(status);
    }

    /**
     * Get zone status color for UI display
     *
     * @return color code for zone status
     */
    public String getStatusColor() {
        if (needsAttention()) {
            return "red";
        } else if (isAtCapacity()) {
            return "orange";
        } else if ("ACTIVE".equals(status)) {
            return "green";
        } else {
            return "gray";
        }
    }
}
