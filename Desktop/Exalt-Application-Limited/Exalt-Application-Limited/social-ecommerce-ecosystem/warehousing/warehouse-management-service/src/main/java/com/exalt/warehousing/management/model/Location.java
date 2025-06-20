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
 * Entity representing a storage location within a warehouse zone
 */
@Entity
@Table(name = "location")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Location extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private Zone zone;

    @Column(name = "location_code", nullable = false, unique = true)
    private String locationCode;

    @Column(name = "description")
    private String description;

    @Column(name = "name")
    private String name;

    @Column(name = "aisle", nullable = false)
    private String aisle;

    @Column(name = "rack", nullable = false)
    private String rack;

    @Column(name = "level", nullable = false)
    private String level;

    @Column(name = "position", nullable = false)
    private String position;

    @Column(name = "barcode")
    private String barcode;

    @Column(name = "max_weight")
    private Double maxWeight;

    @Column(name = "max_volume")
    private Double maxVolume;

    @Column(name = "max_height")
    private Double maxHeight;

    @Column(name = "max_width")
    private Double maxWidth;

    @Column(name = "max_depth")
    private Double maxDepth;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LocationStatus status;

    @Column(name = "notes")
    private String notes;

    @ElementCollection
    @CollectionTable(name = "location_dimension", joinColumns = @JoinColumn(name = "location_id"))
    @MapKeyColumn(name = "dimension_key")
    @Column(name = "dimension_value")
    private Map<String, String> dimensions = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "location_metadata", joinColumns = @JoinColumn(name = "location_id"))
    @MapKeyColumn(name = "metadata_key")
    @Column(name = "metadata_value")
    private Map<String, String> metadata = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "location_property", joinColumns = @JoinColumn(name = "location_id"))
    @MapKeyColumn(name = "property_key")
    @Column(name = "property_value")
    private Map<String, String> properties = new HashMap<>();

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = LocationStatus.AVAILABLE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Get the zone ID as String
     * 
     * @return the zone ID as String
     */
    @Transient
    public String getZoneId() {
        return zone != null ? zone.getId() : null;
    }
    
    /**
     * Get the zone ID as UUID
     * 
     * @return the zone ID as UUID
     */
    @Transient
    public UUID getZoneIdAsUUID() {
        String id = getZoneId();
        return id != null ? UUID.fromString(id) : null;
    }
    
    /**
     * Set the zone ID (helper method)
     * 
     * @param zoneId the zone ID
     */
    public void setZoneId(String zoneId) {
        if (zone == null) {
            zone = new Zone();
        }
        zone.setId(zoneId);
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
    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Double getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(Double maxWeight) {
        this.maxWeight = maxWeight;
    }

    public Double getMaxVolume() {
        return maxVolume;
    }

    public void setMaxVolume(Double maxVolume) {
        this.maxVolume = maxVolume;
    }

    public Double getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(Double maxHeight) {
        this.maxHeight = maxHeight;
    }

    public Double getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(Double maxWidth) {
        this.maxWidth = maxWidth;
    }

    public Double getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(Double maxDepth) {
        this.maxDepth = maxDepth;
    }

    public LocationStatus getStatus() {
        return status;
    }

    public void setStatus(LocationStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Map<String, String> getDimensions() {
        return dimensions;
    }

    public void setDimensions(Map<String, String> dimensions) {
        this.dimensions = dimensions;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
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

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }
} 
