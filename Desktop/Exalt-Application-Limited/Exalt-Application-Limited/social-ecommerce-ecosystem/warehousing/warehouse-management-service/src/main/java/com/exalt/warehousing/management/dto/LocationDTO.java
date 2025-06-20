package com.exalt.warehousing.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Data Transfer Object for Location entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO {
    
    private UUID id;
    
    @NotNull(message = "Warehouse ID is required")
    private UUID warehouseId;
    
    @NotNull(message = "Zone ID is required")
    private UUID zoneId;
    
    @NotBlank(message = "Location code is required")
    @Size(min = 1, max = 50, message = "Location code must be between 1 and 50 characters")
    private String locationCode;
    
    @NotBlank(message = "Location name is required")
    @Size(min = 1, max = 100, message = "Location name must be between 1 and 100 characters")
    private String locationName;
    
    private String locationType;
    
    private Integer capacity;
    
    private Double length;
    
    private Double width;
    
    private Double height;
    
    private Double maxWeight;
    
    private String status;
    
    private String temperatureZone;
    
    private Boolean hazardousItemsAllowed;
    
    private List<String> restrictions;
    
    private Integer currentItems;
    
    private Double currentWeight;
    
    private Double utilizationPercentage;
    
    private String notes;
    
    private LocalDateTime lastStocktakeDate;
    
    private Boolean needsReview;
    
    private String qrCodeContent;
    
    private UUID assignedStaffId;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private Boolean deleted;
    
    // Additional detailed fields required by mappers/controllers
    private String description;
    private String aisle;
    private String rack;
    private String level;
    private String position;
    private String barcode;
    
    private Double maxWeightKg;
    private Double maxVolumeCubicMeters;
    private Double heightCm;
    private Double widthCm;
    private Double depthCm;
    
    private Map<String, Object> metadata;
    private Map<String, Object> properties;
    
    private Integer maxItems;
    
    // Explicit getters and setters to ensure they are available during compilation
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(UUID warehouseId) {
        this.warehouseId = warehouseId;
    }

    public UUID getZoneId() {
        return zoneId;
    }

    public void setZoneId(UUID zoneId) {
        this.zoneId = zoneId;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(Double maxWeight) {
        this.maxWeight = maxWeight;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTemperatureZone() {
        return temperatureZone;
    }

    public void setTemperatureZone(String temperatureZone) {
        this.temperatureZone = temperatureZone;
    }

    public Boolean getHazardousItemsAllowed() {
        return hazardousItemsAllowed;
    }

    public void setHazardousItemsAllowed(Boolean hazardousItemsAllowed) {
        this.hazardousItemsAllowed = hazardousItemsAllowed;
    }

    public List<String> getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(List<String> restrictions) {
        this.restrictions = restrictions;
    }

    public Integer getCurrentItems() {
        return currentItems;
    }

    public void setCurrentItems(Integer currentItems) {
        this.currentItems = currentItems;
    }

    public Double getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(Double currentWeight) {
        this.currentWeight = currentWeight;
    }

    public Double getUtilizationPercentage() {
        return utilizationPercentage;
    }

    public void setUtilizationPercentage(Double utilizationPercentage) {
        this.utilizationPercentage = utilizationPercentage;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getLastStocktakeDate() {
        return lastStocktakeDate;
    }

    public void setLastStocktakeDate(LocalDateTime lastStocktakeDate) {
        this.lastStocktakeDate = lastStocktakeDate;
    }

    public Boolean getNeedsReview() {
        return needsReview;
    }

    public void setNeedsReview(Boolean needsReview) {
        this.needsReview = needsReview;
    }

    public String getQrCodeContent() {
        return qrCodeContent;
    }

    public void setQrCodeContent(String qrCodeContent) {
        this.qrCodeContent = qrCodeContent;
    }

    public UUID getAssignedStaffId() {
        return assignedStaffId;
    }

    public void setAssignedStaffId(UUID assignedStaffId) {
        this.assignedStaffId = assignedStaffId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAisle() {
        return aisle;
    }

    public void setAisle(String aisle) {
        this.aisle = aisle;
    }

    public String getRack() {
        return rack;
    }

    public void setRack(String rack) {
        this.rack = rack;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Double getMaxWeightKg() {
        return maxWeightKg;
    }

    public void setMaxWeightKg(Double maxWeightKg) {
        this.maxWeightKg = maxWeightKg;
    }

    public Double getMaxVolumeCubicMeters() {
        return maxVolumeCubicMeters;
    }

    public void setMaxVolumeCubicMeters(Double maxVolumeCubicMeters) {
        this.maxVolumeCubicMeters = maxVolumeCubicMeters;
    }

    public Double getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(Double heightCm) {
        this.heightCm = heightCm;
    }

    public Double getWidthCm() {
        return widthCm;
    }

    public void setWidthCm(Double widthCm) {
        this.widthCm = widthCm;
    }

    public Double getDepthCm() {
        return depthCm;
    }

    public void setDepthCm(Double depthCm) {
        this.depthCm = depthCm;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public Integer getMaxItems() {
        return maxItems;
    }

    public void setMaxItems(Integer maxItems) {
        this.maxItems = maxItems;
    }
}