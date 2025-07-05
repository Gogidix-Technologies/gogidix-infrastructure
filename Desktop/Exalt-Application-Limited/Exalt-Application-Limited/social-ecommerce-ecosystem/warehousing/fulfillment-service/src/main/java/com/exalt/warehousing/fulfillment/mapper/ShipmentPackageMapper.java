package com.exalt.warehousing.fulfillment.mapper;

import com.exalt.warehousing.fulfillment.dto.ShipmentPackageDTO;
import com.exalt.warehousing.fulfillment.entity.ShipmentPackage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.UUID;

/**
 * Mapper for converting between ShipmentPackage entity and DTO
 */
@Mapper(componentModel = "spring")
public interface ShipmentPackageMapper {

    /**
     * Convert entity to DTO
     * 
     * @param shipmentPackage the entity to convert
     * @return the DTO
     */
    @Mapping(target = "fulfillmentOrderId", source = "fulfillmentOrderId")
    @Mapping(target = "volume", expression = "java(shipmentPackage.getVolume())")
    ShipmentPackageDTO toDTO(ShipmentPackage shipmentPackage);

    /**
     * Convert DTO to entity
     * 
     * @param dto the DTO to convert
     * @return the entity
     */
    @Mapping(target = "fulfillmentOrder", ignore = true)
    ShipmentPackage toEntity(ShipmentPackageDTO dto);
} 
