package com.exalt.warehousing.fulfillment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO for specifying inventory allocation criteria
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocationCriteria {
    private String allocationStrategy; // FIFO, FEFO, NEAREST_LOCATION, etc.
    private boolean considerExpiration;
    private boolean considerLocationZoning;
    private boolean respectInventoryReservations;
    private List<String> priorityLocationTypes;
    private Map<String, Integer> locationPriorities;
    private double maxDistanceMeters;
    private int maxLocationCount;
}
