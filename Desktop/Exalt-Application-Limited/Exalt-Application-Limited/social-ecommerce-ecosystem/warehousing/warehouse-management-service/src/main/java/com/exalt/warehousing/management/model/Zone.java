package com.exalt.warehousing.management.model;

import com.exalt.warehousing.shared.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Entity representing a zone within a warehouse
 */
@Entity
@Table(name = "zone")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Zone extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code", nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ZoneType type;

    @Column(name = "description")
    private String description;

    @Column(name = "square_footage")
    private Double squareFootage;

    @Column(name = "max_capacity")
    private Integer maxCapacity;

    @Column(name = "temperature_range_min")
    private Double temperatureRangeMin;

    @Column(name = "temperature_range_max")
    private Double temperatureRangeMax;

    @Column(name = "humidity_range_min")
    private Double humidityRangeMin;

    @Column(name = "humidity_range_max")
    private Double humidityRangeMax;

    @ElementCollection
    @CollectionTable(name = "zone_metadata", joinColumns = @JoinColumn(name = "zone_id"))
    @MapKeyColumn(name = "metadata_key")
    @Column(name = "metadata_value")
    private Map<String, String> metadata = new HashMap<>();

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Get the warehouse ID as String
     *
     * @return the warehouse ID as String
     */
    @Transient
    public String getWarehouseId() {
        return warehouse != null ? warehouse.getId() : null;
    }
    
    /**
     * Get the warehouse ID as UUID
     *
     * @return the warehouse ID as UUID
     */
    @Transient
    public UUID getWarehouseIdAsUUID() {
        String id = getWarehouseId();
        return id != null ? UUID.fromString(id) : null;
    }

    /**
     * Set the warehouse ID (helper method)
     *
     * @param warehouseId the warehouse ID
     */
    public void setWarehouseId(String warehouseId) {
        if (warehouse == null) {
            warehouse = new Warehouse();
        }
        warehouse.setId(warehouseId);
    }

    // Adding explicit getter and setter methods to ensure they are available during compilation
    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ZoneType getType() {
        return type;
    }

    public void setType(ZoneType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getSquareFootage() {
        return squareFootage;
    }

    public void setSquareFootage(Double squareFootage) {
        this.squareFootage = squareFootage;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Double getTemperatureRangeMin() {
        return temperatureRangeMin;
    }

    public void setTemperatureRangeMin(Double temperatureRangeMin) {
        this.temperatureRangeMin = temperatureRangeMin;
    }

    public Double getTemperatureRangeMax() {
        return temperatureRangeMax;
    }

    public void setTemperatureRangeMax(Double temperatureRangeMax) {
        this.temperatureRangeMax = temperatureRangeMax;
    }

    public Double getHumidityRangeMin() {
        return humidityRangeMin;
    }

    public void setHumidityRangeMin(Double humidityRangeMin) {
        this.humidityRangeMin = humidityRangeMin;
    }

    public Double getHumidityRangeMax() {
        return humidityRangeMax;
    }

    public void setHumidityRangeMax(Double humidityRangeMax) {
        this.humidityRangeMax = humidityRangeMax;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
} 
