package com.exalt.warehousing.fulfillment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * DTO for warehouse assignment
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseAssignmentDTO {

    /**
     * ID of the warehouse to assign
     */
    @NotNull(message = "Warehouse ID is required")
    private UUID warehouseId;
} 
