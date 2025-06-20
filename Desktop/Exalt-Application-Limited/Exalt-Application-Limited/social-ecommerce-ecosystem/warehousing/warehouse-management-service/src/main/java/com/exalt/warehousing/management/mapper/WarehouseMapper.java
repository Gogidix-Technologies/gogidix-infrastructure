package com.exalt.warehousing.management.mapper;

import com.exalt.warehousing.management.dto.WarehouseDTO;
import com.exalt.warehousing.management.model.Warehouse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between Warehouse entity and DTO
 */
@Component
public class WarehouseMapper {
    
    /**
     * Convert Warehouse entity to WarehouseDTO
     * 
     * @param warehouse the entity to convert
     * @return the converted DTO
     */
    public WarehouseDTO toDTO(Warehouse warehouse) {
        if (warehouse == null) {
            return null;
        }
        
        return WarehouseDTO.builder()
                .id(UUID.fromString(warehouse.getId()))
                .name(warehouse.getName())
                .code(warehouse.getCode())
                .addressLine1(warehouse.getAddressLine1())
                .addressLine2(warehouse.getAddressLine2())
                .city(warehouse.getCity())
                .state(warehouse.getState())
                .postalCode(warehouse.getPostalCode())
                .country(warehouse.getCountry())
                .phoneNumber(warehouse.getPhoneNumber())
                .email(warehouse.getEmail())
                .squareFootage(warehouse.getSquareFootage())
                .status(warehouse.getStatus())
                .isActive(warehouse.getIsActive())
                .createdAt(warehouse.getCreatedAt())
                .updatedAt(warehouse.getUpdatedAt())
                .build();
    }
    
    /**
     * Convert WarehouseDTO to Warehouse entity
     * 
     * @param warehouseDTO the DTO to convert
     * @return the converted entity
     */
    public Warehouse toEntity(WarehouseDTO warehouseDTO) {
        if (warehouseDTO == null) {
            return null;
        }
        
        return Warehouse.builder()
                .id(warehouseDTO.getId() != null ? warehouseDTO.getId().toString() : null)
                .name(warehouseDTO.getName())
                .code(warehouseDTO.getCode())
                .addressLine1(warehouseDTO.getAddressLine1())
                .addressLine2(warehouseDTO.getAddressLine2())
                .city(warehouseDTO.getCity())
                .state(warehouseDTO.getState())
                .postalCode(warehouseDTO.getPostalCode())
                .country(warehouseDTO.getCountry())
                .phoneNumber(warehouseDTO.getPhoneNumber())
                .email(warehouseDTO.getEmail())
                .squareFootage(warehouseDTO.getSquareFootage())
                .status(warehouseDTO.getStatus())
                .isActive(warehouseDTO.getIsActive())
                .build();
    }
    
    /**
     * Update Warehouse entity from WarehouseDTO
     * 
     * @param warehouse the entity to update
     * @param warehouseDTO the DTO with new values
     * @return the updated entity
     */
    public Warehouse updateEntityFromDTO(Warehouse warehouse, WarehouseDTO warehouseDTO) {
        if (warehouseDTO == null) {
            return warehouse;
        }
        
        if (warehouseDTO.getName() != null) {
            warehouse.setName(warehouseDTO.getName());
        }
        if (warehouseDTO.getCode() != null) {
            warehouse.setCode(warehouseDTO.getCode());
        }
        if (warehouseDTO.getAddressLine1() != null) {
            warehouse.setAddressLine1(warehouseDTO.getAddressLine1());
        }
        warehouse.setAddressLine2(warehouseDTO.getAddressLine2());
        if (warehouseDTO.getCity() != null) {
            warehouse.setCity(warehouseDTO.getCity());
        }
        if (warehouseDTO.getState() != null) {
            warehouse.setState(warehouseDTO.getState());
        }
        if (warehouseDTO.getPostalCode() != null) {
            warehouse.setPostalCode(warehouseDTO.getPostalCode());
        }
        if (warehouseDTO.getCountry() != null) {
            warehouse.setCountry(warehouseDTO.getCountry());
        }
        warehouse.setPhoneNumber(warehouseDTO.getPhoneNumber());
        warehouse.setEmail(warehouseDTO.getEmail());
        warehouse.setSquareFootage(warehouseDTO.getSquareFootage());
        if (warehouseDTO.getStatus() != null) {
            warehouse.setStatus(warehouseDTO.getStatus());
        }
        if (warehouseDTO.getIsActive() != null) {
            warehouse.setIsActive(warehouseDTO.getIsActive());
        }
        
        return warehouse;
    }
    
    /**
     * Convert list of Warehouse entities to list of WarehouseDTO
     * 
     * @param warehouses the list of entities to convert
     * @return the list of converted DTOs
     */
    public List<WarehouseDTO> toDTOList(List<Warehouse> warehouses) {
        return warehouses.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
} 
